package com.revenuecat.samplecat.ui.screens.offerings

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
import com.revenuecat.samplecat.ui.components.ConceptIntroduction
import com.revenuecat.samplecat.ui.components.ContentBackground
import com.revenuecat.samplecat.ui.components.OfferingCard
import com.revenuecat.samplecat.ui.components.spinner.SpinnerContainer
import com.revenuecat.samplecat.ui.components.spinner.SpinnerPullToRefreshIndicator
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
    val error by userViewModel.error.collectAsState()

    // Fetch offerings on first load
    LaunchedEffect(Unit) {
        if (offerings == null) {
            userViewModel.fetchOfferings()
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    Box(modifier = modifier.fillMaxSize()) {
        ContentBackground(color = RCGreen)

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
                        imageRes = R.drawable.visual_offerings,
                        title = stringResource(R.string.offerings_title),
                        description = stringResource(R.string.offerings_description)
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
                                text = stringResource(R.string.offerings_empty),
                                color = Color(0xFFE65100),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

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
                    SpinnerContainer()
                }
            }
        }
    }
}
