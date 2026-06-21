package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import javax.swing.JFileChooser

@Composable
actual fun SystemFilePicker(
    onFileChosen: (ByteArray) -> Unit,
    dismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            dialogTitle = "Choose a file"
        }
        val result = chooser.showOpenDialog(null)
        when (result) {
            JFileChooser.APPROVE_OPTION -> {
                val stream = chooser.selectedFile.inputStream()
                val bytes = stream.use { it.readBytes() }
                onFileChosen(bytes)
            }
            JFileChooser.CANCEL_OPTION -> dismiss()
            else -> TODO()
        }
    }
}