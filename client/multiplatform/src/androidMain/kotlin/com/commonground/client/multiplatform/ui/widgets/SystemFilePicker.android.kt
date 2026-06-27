package com.commonground.client.multiplatform.ui.widgets

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun SystemFilePicker(
    onFileChosen: (ByteArray) -> Unit,
    dismiss: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val stream = context.contentResolver.openInputStream(uri)
            if (stream != null) {
                val bytes = stream.use { it.readBytes() }
                onFileChosen(bytes)
            } else {
                dismiss()
            }
        } else {
            dismiss()
        }
    }
    LaunchedEffect(Unit) {
        launcher.launch(arrayOf("image/*"))
    }
}