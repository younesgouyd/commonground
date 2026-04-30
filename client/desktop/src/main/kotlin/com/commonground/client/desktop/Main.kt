package com.commonground.client.desktop

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.commonground.client.multiplatform.data.RepoStore
import com.commonground.client.multiplatform.ui.MainUi

fun main() {
    val repoStore = RepoStore()

    application {
        Window(
            state = rememberWindowState(
                placement = WindowPlacement.Maximized,
                position = WindowPosition(Alignment.Center)
            ),
            onCloseRequest = { exitApplication() }
        ) {
            MainUi(repoStore)
        }
    }
}