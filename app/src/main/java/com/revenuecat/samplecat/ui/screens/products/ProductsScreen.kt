package com.revenuecat.samplecat.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.revenuecat.samplecat.R
import com.revenuecat.samplecat.model.PurchasableState
import com.revenuecat.samplecat.ui.components.ConceptIntroduction
import com.revenuecat.samplecat.ui.components.ContentBackground
import com.revenuecat.samplecat.ui.components.PurchasableCard
import com.revenuecat.samplecat.ui.utils.getActivity
import com.revenuecat.samplecat.ui.components.spinner.SpinnerContainer
import com.revenuecat.samplecat.ui.components.spinner.SpinnerPullToRefreshIndicator
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
    val error by userViewModel.error.collectAsState()

    val activity = getActivity()

    // Fetch offerings on first load
    LaunchedEffect(Unit) {
        if (offerings == null) {
            userViewModel.fetchOfferings()
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    Box(modifier = modifier.fillMaxSize()) {
        ContentBackground(color = AccentColor)

        PullToRefreshBox(
            isRefreshing = isFetching,
            onRefresh = { userViewModel.fetchOfferings() },
            state = pullToRefreshState,
            indicator = {
                SpinnerPullToRefreshIndicator(
                    state = pullToRefreshState,
                    isRefreshing = isFetching,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    ConceptIntroduction(
                        imageRes = R.drawable.visual_products,
                        title = stringResource(R.string.products_title),
                        description = stringResource(R.string.products_description)
                    )
                }

                // Show error if present
                error?.let { errorMessage ->
                    item {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFCDD2))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFB71C1C),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                val products = userViewModel.getAllProducts()

                // Show empty state if no products and not loading
                if (products.isEmpty() && !isFetching && error == null) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF3E0))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.products_empty),
                                color = Color(0xFFE65100),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

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
                    SpinnerContainer()
                }
            }
        }
    }
}
