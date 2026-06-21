package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.commonground.client.multiplatform.ui.toBackendUrl
import com.commonground.core.models.ImageUrl

@Composable
fun ImagePreviewDialog(
    imageUrl: ImageUrl,
    onDismiss: () -> Unit
) {
    var showSystemFileSaver by remember { mutableStateOf(false) }

    if (showSystemFileSaver) {
        SystemFileSaver(imageUrl) { showSystemFileSaver = false }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                url = imageUrl.toBackendUrl(),
                contentScale = ContentScale.Fit
            )
            FilledIconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.25f),
                    contentColor = Color.White
                ),
                onClick = { showSystemFileSaver = true },
                content = { Icon(imageVector = Icons.Default.Download, contentDescription = "Save Image") }
            )
        }
    }
}