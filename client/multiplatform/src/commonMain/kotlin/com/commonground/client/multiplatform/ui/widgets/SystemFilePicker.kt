package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.runtime.Composable

@Composable
expect fun SystemFilePicker(
    onFileChosen: (ByteArray) -> Unit,
    dismiss: () -> Unit
)