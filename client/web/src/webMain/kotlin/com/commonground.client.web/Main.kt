package com.commonground.client.web

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import com.commonground.client.multiplatform.ui.MainUi

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        MainUi(Modifier.fillMaxSize())
    }
}
