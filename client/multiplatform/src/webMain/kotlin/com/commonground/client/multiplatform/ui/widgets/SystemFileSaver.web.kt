package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.commonground.client.multiplatform.ui.toBackendUrl
import kotlinx.browser.window

@Composable
actual fun SystemFileSaver(
    fileUrl: String,
    dismiss: () -> Unit
) {
    val fullUrl = fileUrl.toBackendUrl()

    LaunchedEffect(fileUrl) {
        window.open(fullUrl, "_blank")
        dismiss()
    }
}