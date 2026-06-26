package com.commonground.client.desktop

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import com.commonground.client.multiplatform.data.JvmFileStorage
import com.commonground.client.multiplatform.ui.MainUi

fun main() {
    val fileStorage = JvmFileStorage()

    application {
        Window(
            state = rememberWindowState(
                placement = WindowPlacement.Maximized,
                position = WindowPosition(Alignment.Center)
            ),
            onCloseRequest = { exitApplication() }
        ) {
            MainUi(fileStorage = fileStorage)
        }
    }
}