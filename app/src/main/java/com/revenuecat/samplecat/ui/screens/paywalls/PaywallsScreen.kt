package com.revenuecat.samplecat.ui.screens.paywalls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import com.revenuecat.samplecat.R
import com.revenuecat.samplecat.ui.components.ConceptIntroduction
import com.revenuecat.samplecat.ui.components.ContentBackground
import com.revenuecat.samplecat.ui.components.spinner.SpinnerContainer
import com.revenuecat.samplecat.ui.components.spinner.SpinnerPullToRefreshIndicator
import com.revenuecat.samplecat.ui.theme.RCBlue
import com.revenuecat.samplecat.viewmodel.OfferingsUiState
import com.revenuecat.samplecat.viewmodel.UserViewModel

/**
 * Screen displaying offerings with the ability to show RevenueCat paywalls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallsScreen(
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val offeringsState by userViewModel.offeringsState.collectAsState()

    var selectedOffering by remember { mutableStateOf<Offering?>(null) }

    val pullToRefreshState = rememberPullToRefreshState()

    // Derive values from sealed state
    val isRefreshing = (offeringsState as? OfferingsUiState.Success)?.isRefreshing == true
    val isLoading = offeringsState is OfferingsUiState.Loading

    Box(modifier = modifier.fillMaxSize()) {
        ContentBackground(color = RCBlue)

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { userViewModel.fetchOfferings() },
            state = pullToRefreshState,
            indicator = {
                SpinnerPullToRefreshIndicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
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
                        imageRes = R.drawable.visual_paywalls,
                        title = stringResource(R.string.paywalls_title),
                        description = stringResource(R.string.paywalls_description)
                    )
                }

                when (val state = offeringsState) {
                    is OfferingsUiState.Loading -> {
                        // Loading state handled by overlay below
                    }

                    is OfferingsUiState.Error -> {
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
                                    text = state.message,
                                    color = Color(0xFFB71C1C),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    is OfferingsUiState.Success -> {
                        // Show refresh error if present
                        state.refreshError?.let { errorMessage ->
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

                        val offeringsList = state.offerings.all.values.toList()

                        // Show empty state if no offerings
                        if (offeringsList.isEmpty()) {
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
                                        text = stringResource(R.string.paywalls_empty),
                                        color = Color(0xFFE65100),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        items(offeringsList, key = { it.identifier }) { offering ->
                            PaywallOfferingCard(
                                offering = offering,
                                onClick = { selectedOffering = offering },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Loading overlay for initial load
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SpinnerContainer()
                }
            }
        }
    }

    // Show paywall dialog when an offering is selected
    selectedOffering?.let { offering ->
        PaywallDialog(
            paywallDialogOptions = PaywallDialogOptions.Builder()
                .setOffering(offering)
                .setDismissRequest { selectedOffering = null }
                .build()
        )
    }
}

@Composable
private fun PaywallOfferingCard(
    offering: Offering,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = offering.identifier,
                style = MaterialTheme.typography.titleMedium
            )
            val packageCount = offering.availablePackages.size
            val packageText = when (packageCount) {
                0 -> stringResource(R.string.package_count_zero)
                1 -> stringResource(R.string.package_count_one)
                else -> stringResource(R.string.package_count_other, packageCount)
            }
            Text(
                text = packageText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
