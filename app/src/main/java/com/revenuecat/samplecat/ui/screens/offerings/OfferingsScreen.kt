package com.revenuecat.samplecat.ui.screens.offerings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.revenuecat.samplecat.ui.components.ConceptIntroduction
import com.revenuecat.samplecat.ui.components.ContentBackground
import com.revenuecat.samplecat.ui.components.OfferingCard
import com.revenuecat.samplecat.ui.theme.RCGreen
import com.revenuecat.samplecat.viewmodel.UserViewModel

/**
 * Screen displaying the list of available RevenueCat offerings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferingsScreen(
    userViewModel: UserViewModel,
    onOfferingClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val offerings by userViewModel.offerings.collectAsState()
    val isFetching by userViewModel.isFetchingOfferings.collectAsState()

    // Fetch offerings on first load
    LaunchedEffect(Unit) {
        if (offerings == null) {
            userViewModel.fetchOfferings()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ContentBackground(color = RCGreen)

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
                        imageRes = R.drawable.visual_offerings,
                        title = "Offerings",
                        description = "Offerings are the products you can \"offer\" to customers on your paywall."
                    )
                }

                val offeringsList = userViewModel.getOfferingsList()

                items(offeringsList, key = { it.identifier }) { offering ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        OfferingCard(
                            offering = offering,
                            onClick = { onOfferingClick(offering.identifier) }
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
