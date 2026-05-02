package com.commonground.client.multiplatform.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
actual fun getWindowSizeClass(): WindowWidthSizeClass {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
        ?: return WindowWidthSizeClass.Compact

    return calculateWindowSizeClass(activity).widthSizeClass
}