package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.time.Duration


@Composable
fun Duration(
    value: Duration,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Timer, null)
            Text(
                text = value.formatted(),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun Duration?.formatted(): String {
    if (this == null) {
        return "??:??"
    }
    return this.toComponents { hours: Long, minutes: Int, seconds: Int, _: Int ->
        StringBuilder()
            .append(hours.toString().padStart(2, '0'))
            .append(":")
            .append(minutes.toString().padStart(2, '0'))
            .append(":")
            .append(seconds.toString().padStart(2, '0'))
            .toString()
    }
}