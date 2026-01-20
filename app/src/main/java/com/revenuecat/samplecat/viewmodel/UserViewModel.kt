package com.revenuecat.samplecat.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesErrorCode
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.samplecat.config.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel that manages RevenueCat offerings, products, and purchases.
 *
 * This follows RevenueCat's best practices for Android integration
 * and can serve as a reference for your own app's implementation.
 */
class UserViewModel : ViewModel() {

    private val _customerInfo = MutableStateFlow<CustomerInfo?>(null)
    val customerInfo: StateFlow<CustomerInfo?> = _customerInfo.asStateFlow()

    private val _offerings = MutableStateFlow<Offerings?>(null)
    val offerings: StateFlow<Offerings?> = _offerings.asStateFlow()

    private val _isFetchingOfferings = MutableStateFlow(false)
    val isFetchingOfferings: StateFlow<Boolean> = _isFetchingOfferings.asStateFlow()

    private val _isPurchasing = MutableStateFlow(false)
    val isPurchasing: StateFlow<Boolean> = _isPurchasing.asStateFlow()

    private val _purchasingProductId = MutableStateFlow<String?>(null)
    val purchasingProductId: StateFlow<String?> = _purchasingProductId.asStateFlow()

    private val _subscriptionActive = MutableStateFlow(false)
    val subscriptionActive: StateFlow<Boolean> = _subscriptionActive.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val customerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
        _customerInfo.value = customerInfo
        updateSubscriptionStatus(customerInfo)
    }

    init {
        // Register listener for CustomerInfo updates
        Purchases.sharedInstance.updatedCustomerInfoListener = customerInfoListener

        // Fetch initial customer info
        viewModelScope.launch {
            try {
                val info = Purchases.sharedInstance.awaitCustomerInfo()
                _customerInfo.value = info
                updateSubscriptionStatus(info)
            } catch (_: PurchasesException) {
                // Initial fetch failed, will be updated via listener
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up listener when ViewModel is destroyed
        Purchases.sharedInstance.updatedCustomerInfoListener = null
    }

    private fun updateSubscriptionStatus(customerInfo: CustomerInfo?) {
        val entitlementId = Constants.entitlementId ?: return
        _subscriptionActive.value = customerInfo?.entitlements?.get(entitlementId)?.isActive == true
    }

    /**
     * Fetches the available offerings from RevenueCat.
     */
    fun fetchOfferings() {
        viewModelScope.launch {
            _isFetchingOfferings.value = true
            _error.value = null

            Log.d(TAG, "Fetching offerings...")

            try {
                val fetchedOfferings = Purchases.sharedInstance.awaitOfferings()
                _offerings.value = fetchedOfferings

                Log.d(TAG, "Fetched offerings: ${fetchedOfferings.all.size} offerings")
                fetchedOfferings.all.forEach { (id, offering) ->
                    Log.d(TAG, "  Offering: $id with ${offering.availablePackages.size} packages")
                }

                if (fetchedOfferings.all.isEmpty()) {
                    Log.w(TAG, "No offerings returned from RevenueCat. Check your dashboard configuration.")
                }
            } catch (e: PurchasesException) {
                Log.e(TAG, "Failed to fetch offerings: ${e.message}", e)
                _error.value = "Failed to fetch offerings: ${e.message}"
            } finally {
                _isFetchingOfferings.value = false
            }
        }
    }

    companion object {
        private const val TAG = "UserViewModel"
    }

    /**
     * Purchases a package.
     *
     * @param activity The activity to use for the purchase flow
     * @param packageToPurchase The package to purchase
     */
    fun purchase(activity: Activity, packageToPurchase: Package) {
        if (_isPurchasing.value) return

        viewModelScope.launch {
            _isPurchasing.value = true
            _purchasingProductId.value = packageToPurchase.product.id
            _error.value = null

            try {
                val purchaseParams = PurchaseParams.Builder(activity, packageToPurchase).build()
                val result = Purchases.sharedInstance.awaitPurchase(purchaseParams)
                _customerInfo.value = result.customerInfo
                updateSubscriptionStatus(result.customerInfo)
            } catch (e: PurchasesException) {
                // User cancelled is not an error
                if (e.error.code != PurchasesErrorCode.PurchaseCancelledError) {
                    _error.value = "Purchase failed: ${e.message}"
                }
            } finally {
                _isPurchasing.value = false
                _purchasingProductId.value = null
            }
        }
    }

    /**
     * Purchases a store product directly.
     *
     * @param activity The activity to use for the purchase flow
     * @param product The store product to purchase
     */
    fun purchase(activity: Activity, product: StoreProduct) {
        if (_isPurchasing.value) return

        viewModelScope.launch {
            _isPurchasing.value = true
            _purchasingProductId.value = product.id
            _error.value = null

            try {
                val purchaseParams = PurchaseParams.Builder(activity, product).build()
                val result = Purchases.sharedInstance.awaitPurchase(purchaseParams)
                _customerInfo.value = result.customerInfo
                updateSubscriptionStatus(result.customerInfo)
            } catch (e: PurchasesException) {
                // User cancelled is not an error
                if (e.error.code != PurchasesErrorCode.PurchaseCancelledError) {
                    _error.value = "Purchase failed: ${e.message}"
                }
            } finally {
                _isPurchasing.value = false
                _purchasingProductId.value = null
            }
        }
    }

    /**
     * Checks if a product has been purchased.
     *
     * @param productId The product identifier to check
     * @return True if the product has been purchased
     */
    fun isProductPurchased(productId: String): Boolean {
        val customerInfo = _customerInfo.value ?: return false
        return customerInfo.allPurchasedProductIds.contains(productId)
    }

    /**
     * Gets all offerings as a list.
     */
    fun getOfferingsList(): List<Offering> {
        return _offerings.value?.all?.values?.toList() ?: emptyList()
    }

    /**
     * Gets all unique products from all offerings.
     */
    fun getAllProducts(): List<StoreProduct> {
        val offerings = _offerings.value ?: return emptyList()
        return offerings.all.values
            .flatMap { offering -> offering.availablePackages }
            .map { pkg -> pkg.product }
            .distinctBy { it.id }
    }

    /**
     * Clears the current error.
     */
    fun clearError() {
        _error.value = null
    }
}
