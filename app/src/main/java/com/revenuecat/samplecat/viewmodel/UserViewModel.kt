package com.revenuecat.samplecat.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.samplecat.config.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    init {
        // Listen to CustomerInfo updates from RevenueCat
        viewModelScope.launch {
            Purchases.sharedInstance.updatedCustomerInfoFlow.collect { newCustomerInfo ->
                _customerInfo.value = newCustomerInfo
                updateSubscriptionStatus(newCustomerInfo)
            }
        }
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

            try {
                val fetchedOfferings = Purchases.sharedInstance.awaitOfferings()
                _offerings.value = fetchedOfferings
            } catch (e: PurchasesException) {
                _error.value = "Failed to fetch offerings: ${e.message}"
            } finally {
                _isFetchingOfferings.value = false
            }
        }
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
                val (_, customerInfo) = Purchases.sharedInstance.awaitPurchase(activity, packageToPurchase)
                _customerInfo.value = customerInfo
                updateSubscriptionStatus(customerInfo)
            } catch (e: PurchasesException) {
                // User cancelled is not an error
                if (!e.userCancelled) {
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
                val (_, customerInfo) = Purchases.sharedInstance.awaitPurchase(activity, product)
                _customerInfo.value = customerInfo
                updateSubscriptionStatus(customerInfo)
            } catch (e: PurchasesException) {
                // User cancelled is not an error
                if (!e.userCancelled) {
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
