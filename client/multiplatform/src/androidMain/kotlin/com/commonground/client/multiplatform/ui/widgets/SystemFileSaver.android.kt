package com.commonground.client.multiplatform.ui.widgets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.commonground.client.multiplatform.ui.toBackendUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI

@Composable
actual fun SystemFileSaver(
    fileUrl: String,
    dismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fileName = fileUrl.substringAfterLast('/').takeIf { it.isNotBlank() } ?: "commonground.jpg"

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/*")
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val url = URI(fileUrl.toBackendUrl()).toURL()
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    url.openStream().use { input ->
                        input.copyTo(output)
                    }
                }
            }
        } else {
            dismiss()
        }
    }

    LaunchedEffect(fileUrl) {
        launcher.launch(fileName)
    }
}