package com.commonground.maptesting

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.style.BaseStyle

@Composable
fun MainUi() {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Map()
        }
    }
}

@Composable
private fun Map() {
//    MaplibreMap(baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"))
    val variant = if (isSystemInDarkTheme()) "dark" else "light" // isSystemInDarkTheme doesn't work on my system (Debian 13 + GNOME 48.7)
    MaplibreMap(
        baseStyle = BaseStyle.Uri("https://api.protomaps.com/styles/v5/$variant/en.json?key=44a27c396e0f154c"),
        options = MapOptions(
            ornamentOptions = OrnamentOptions.AllEnabled
        )
    )
}

@Composable
private fun Event() {
    Card {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                modifier = Modifier.aspectRatio(1f),
                imageVector = Icons.Default.Event,
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter,
                contentDescription = null
            )
            Text(
                text = "Tech meetup",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "meeting tech friends",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By:",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}