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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val offerings by userViewModel.offerings.collectAsState()
    val isFetching by userViewModel.isFetchingOfferings.collectAsState()
    val error by userViewModel.error.collectAsState()

    var selectedOffering by remember { mutableStateOf<Offering?>(null) }

    // Fetch offerings on first load
    LaunchedEffect(Unit) {
        if (offerings == null) {
            userViewModel.fetchOfferings()
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    Box(modifier = modifier.fillMaxSize()) {
        ContentBackground(color = RCBlue)

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
                        imageRes = R.drawable.visual_paywalls,
                        title = "Paywalls",
                        description = "Display beautiful, customizable paywalls to showcase your offerings and drive conversions."
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

                val offeringsList = userViewModel.getOfferingsList()

                // Show empty state if no offerings and not loading
                if (offeringsList.isEmpty() && !isFetching && error == null) {
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
                                text = "No offerings found. Make sure you have configured offerings in your RevenueCat dashboard.",
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
            Text(
                text = "${offering.availablePackages.size} package${if (offering.availablePackages.size != 1) "s" else ""}",
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
