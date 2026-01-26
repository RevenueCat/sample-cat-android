package com.revenuecat.samplecat.ui.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Helper function to get the current Activity from a Composable.
 */
@Composable
fun getActivity(): Activity? {
    val context = LocalContext.current
    return context as? Activity
}
