package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.commonground.client.multiplatform.ui.toBackendUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.swing.JFileChooser

@Composable
actual fun SystemFileSaver(
    fileUrl: String,
    dismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    scope.launch(Dispatchers.IO) {
        val chooser = JFileChooser().apply {
            dialogTitle = "Save Image"
            selectedFile = File(fileUrl.substringAfterLast('/').takeIf { it.isNotBlank() } ?: "commonground.jpg")
        }
        val result = chooser.showSaveDialog(null)
        when (result) {
            JFileChooser.APPROVE_OPTION -> {
                val url = URI(fileUrl.toBackendUrl()).toURL()
                url.openStream().use { input ->
                    Files.copy(
                        input,
                        chooser.selectedFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }
            }
            JFileChooser.CANCEL_OPTION -> dismiss()
            else -> TODO()
        }
    }
}