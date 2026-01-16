package com.revenuecat.samplecat.ui.screens.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.revenuecat.samplecat.R
import com.revenuecat.samplecat.model.PurchasableState
import com.revenuecat.samplecat.ui.components.ConceptIntroduction
import com.revenuecat.samplecat.ui.components.ContentBackground
import com.revenuecat.samplecat.ui.components.PurchasableCard
import com.revenuecat.samplecat.ui.components.getActivity
import com.revenuecat.samplecat.ui.theme.AccentColor
import com.revenuecat.samplecat.viewmodel.UserViewModel

/**
 * Screen displaying all available products from all offerings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val offerings by userViewModel.offerings.collectAsState()
    val isFetching by userViewModel.isFetchingOfferings.collectAsState()
    val isPurchasing by userViewModel.isPurchasing.collectAsState()
    val purchasingProductId by userViewModel.purchasingProductId.collectAsState()

    val activity = getActivity()

    // Fetch offerings on first load
    LaunchedEffect(Unit) {
        if (offerings == null) {
            userViewModel.fetchOfferings()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ContentBackground(color = AccentColor)

        PullToRefreshBox(
            isRefreshing = isFetching,
            onRefresh = { userViewModel.fetchOfferings() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    ConceptIntroduction(
                        imageRes = R.drawable.visual_products,
                        title = "Products",
                        description = "Products are the individual in-app purchases and subscriptions you set up on the Play Store."
                    )
                }

                val products = userViewModel.getAllProducts()

                items(products, key = { it.id }) { product ->
                    val productId = product.id
                    val isPurchased = userViewModel.isProductPurchased(productId)
                    val isThisProductPurchasing = purchasingProductId == productId

                    val state = when {
                        isPurchased -> PurchasableState.Purchased
                        isThisProductPurchasing -> PurchasableState.Purchasing
                        isPurchasing -> PurchasableState.PurchasingOtherProduct
                        else -> PurchasableState.ReadyToPurchase
                    }

                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        PurchasableCard(
                            title = product.title,
                            productId = productId,
                            description = product.description,
                            state = state,
                            onPurchaseClick = {
                                activity?.let { act ->
                                    userViewModel.purchase(act, product)
                                }
                            }
                        )
                    }
                }
            }

            // Loading overlay
            if (isFetching && offerings == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
