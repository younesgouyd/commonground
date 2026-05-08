package com.commonground.maptesting

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*

fun main() {
    application {
        Window(
            state = rememberWindowState(
                placement = WindowPlacement.Maximized,
                position = WindowPosition(Alignment.Center)
            ),
            onCloseRequest = { exitApplication() }
        ) {
            MainUi()
        }
    }
}
