package com.commonground.maptesting

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.coroutines.delay
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import org.w3c.dom.HTMLElement
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Map()
            }
        }
    }
}

@Composable
private fun Map() {
    val cameraState = rememberCameraState()
    val markerPosition = remember { Position(longitude = -7.685091, latitude = 33.538671) }
    LaunchedEffect(Unit) {
        delay(6.seconds)
        cameraState.animateTo(
            finalPosition = CameraPosition(
                target = markerPosition,
                zoom = 7.0
            ),
            duration = 3.seconds
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        MaplibreMap(
            modifier = Modifier.size(600.dp),
            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/${if (isSystemInDarkTheme()) "fiord" else "liberty"}"),
            cameraState = cameraState,
            options = MapOptions(
                ornamentOptions = OrnamentOptions.AllEnabled
            )
        )
        val projection = cameraState.projection
        val screenPoint = remember(cameraState.position, projection) {
            projection?.screenLocationFromPosition(markerPosition)
        }
        if (screenPoint != null) {
            WebMarkerOverlay(screenPoint)
        }
    }
}

@Composable
fun WebMarkerOverlay(offset: DpOffset) {
    val markerElement = remember {
        document.createElement("div").unsafeCast<HTMLElement>().apply {
            style.width = "40px"
            style.height = "40px"
            style.position = "absolute"
            style.zIndex = "10000"        // Force the element over the MapLibre DOM node
            style.setProperty("pointer-events", "none")  // Allows mouse drag events to pass through to the map below

            // Inline SVG matching the appearance of Icons.Default.Place
            innerHTML = """
                <svg width="40" height="40" viewBox="0 0 24 24" fill="#EA4335">
                    <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                </svg>
            """.trimIndent()

            // Anchor adjustment: Centers horizontally, places bottom tip directly on coordinate
            style.transform = "translate(-50%, -100%)"
        }
    }

    // Appends the element when entering composition, removes it when leaving
    DisposableEffect(Unit) {
        onDispose {
            markerElement.remove()
        }
    }

    // Reactively updates layout position whenever coordinates change
    SideEffect {
        val markerHost = findMaplibreHost()
        if (markerHost != null && markerElement.parentElement != markerHost) {
            markerHost.appendChild(markerElement)
        }
        markerElement.style.left = "${offset.x.value}px"
        markerElement.style.top = "${offset.y.value}px"
    }
}

private fun findMaplibreHost(): HTMLElement? {
    val body = document.body ?: return null
    val shadowMap = body.asDynamic().shadowRoot?.querySelector(".maplibregl-map") as? HTMLElement
    return shadowMap ?: (document.querySelector(".maplibregl-map") as? HTMLElement) ?: body
}
