package com.commonground.client.desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import com.commonground.client.multiplatform.ui.MainUi

fun main() {
    application {
        Window(
            state = rememberWindowState(
                placement = WindowPlacement.Maximized,
                position = WindowPosition(Alignment.Center)
            ),
            onCloseRequest = { exitApplication() }
        ) {
            MainUi(Modifier.fillMaxSize())
        }
    }
}