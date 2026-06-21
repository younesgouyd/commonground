package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.runtime.Composable

@Composable
expect fun SystemFileSaver(
    fileUrl: String,
    dismiss: () -> Unit
)