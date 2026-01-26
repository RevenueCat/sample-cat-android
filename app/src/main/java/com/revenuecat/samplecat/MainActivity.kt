package com.revenuecat.samplecat

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.revenuecat.samplecat.ui.navigation.SampleCatNavHost
import com.revenuecat.samplecat.ui.theme.SampleCatTheme
import com.revenuecat.samplecat.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle web purchase redemption from launch intent
        handleWebPurchaseRedemption(intent)

        setContent {
            SampleCatTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SampleCatNavHost(userViewModel = userViewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle web purchase redemption when activity receives new intent (singleTop mode)
        handleWebPurchaseRedemption(intent)
    }

    private fun handleWebPurchaseRedemption(intent: Intent) {
        userViewModel.handleWebPurchaseRedemption(intent)
    }
}
