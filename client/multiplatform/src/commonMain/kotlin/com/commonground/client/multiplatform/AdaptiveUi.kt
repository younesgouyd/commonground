package com.commonground.client.multiplatform

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable


// https://kotlinlang.org/docs/multiplatform/whats-new-compose-170.html#new-common-modules:~:text=The%20calculateWindowSizeClass()%20function%20is%20not%20available%20in%20common%20code%20yet
@Composable
expect fun getWindowSizeClass(): WindowWidthSizeClass

@Composable
fun AdaptiveUi(
    wide: @Composable () -> Unit,
    compact: @Composable () -> Unit
) {
    val windowSizeClass = getWindowSizeClass()

    when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> compact()
        else -> wide()
    }
}