package com.revenuecat.samplecat.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.revenuecat.samplecat.R
import com.revenuecat.samplecat.ui.screens.customercenter.CustomerCenterScreen
import com.revenuecat.samplecat.ui.screens.offerings.OfferingPackagesScreen
import com.revenuecat.samplecat.ui.screens.offerings.OfferingsScreen
import com.revenuecat.samplecat.ui.screens.paywalls.PaywallsScreen
import com.revenuecat.samplecat.ui.screens.products.ProductsScreen
import com.revenuecat.samplecat.viewmodel.RedemptionState
import com.revenuecat.samplecat.viewmodel.UserViewModel

/**
 * Sealed class representing the navigation routes.
 */
sealed class Screen(val route: String, @StringRes val titleRes: Int, val icon: ImageVector?) {
    data object Offerings : Screen("offerings", R.string.nav_offerings, Icons.Default.Payments)
    data object Products : Screen("products", R.string.nav_products, Icons.Default.Inventory2)
    data object Paywalls : Screen("paywalls", R.string.nav_paywalls, Icons.Default.CreditCard)
    data object CustomerCenter : Screen("customer_center", R.string.nav_support, Icons.Default.ManageAccounts)
    data object OfferingPackages : Screen(
        route = "offering_packages/{offeringId}",
        titleRes = R.string.nav_packages,
        icon = null
    ) {
        fun createRoute(offeringId: String) = "offering_packages/$offeringId"
    }
}

private val bottomNavItems = listOf(
    Screen.Offerings,
    Screen.Products,
    Screen.Paywalls,
    Screen.CustomerCenter
)

@Composable
fun SampleCatNavHost(
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val redemptionState by userViewModel.redemptionState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Determine if we should show the bottom bar
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    // Get message for current redemption state
    val redemptionMessage = when (val state = redemptionState) {
        is RedemptionState.Success -> stringResource(state.message.resId)
        is RedemptionState.Error -> if (state.message.formatArgs.isNotEmpty()) {
            stringResource(state.message.resId, *state.message.formatArgs.toTypedArray())
        } else {
            stringResource(state.message.resId)
        }
        is RedemptionState.Expired -> if (state.message.formatArgs.isNotEmpty()) {
            stringResource(state.message.resId, *state.message.formatArgs.toTypedArray())
        } else {
            stringResource(state.message.resId)
        }
        else -> null
    }

    // Handle redemption state changes with snackbar
    LaunchedEffect(redemptionState, redemptionMessage) {
        if (redemptionMessage != null) {
            snackbarHostState.showSnackbar(redemptionMessage)
            userViewModel.clearRedemptionState()
        }
    }

    // Show loading dialog when redeeming
    if (redemptionState is RedemptionState.Redeeming) {
        RedemptionLoadingDialog()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val title = stringResource(screen.titleRes)
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let { Icon(it, contentDescription = title) }
                            },
                            label = { Text(title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Offerings.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Offerings.route) {
                OfferingsScreen(
                    userViewModel = userViewModel,
                    onOfferingClick = { offeringId ->
                        navController.navigate(Screen.OfferingPackages.createRoute(offeringId))
                    }
                )
            }

            composable(Screen.Products.route) {
                ProductsScreen(userViewModel = userViewModel)
            }

            composable(Screen.Paywalls.route) {
                PaywallsScreen(userViewModel = userViewModel)
            }

            composable(Screen.CustomerCenter.route) {
                CustomerCenterScreen()
            }

            composable(
                route = Screen.OfferingPackages.route,
                arguments = listOf(
                    navArgument("offeringId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val offeringId = backStackEntry.arguments?.getString("offeringId") ?: ""
                OfferingPackagesScreen(
                    userViewModel = userViewModel,
                    offeringId = offeringId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun RedemptionLoadingDialog() {
    Dialog(onDismissRequest = { /* Non-dismissible while loading */ }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
}
