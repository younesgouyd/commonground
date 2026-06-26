package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.commonground.client.multiplatform.Platform
import com.commonground.client.multiplatform.platform
import com.commonground.client.multiplatform.ui.MapViewport
import com.commonground.client.multiplatform.ui.queryEventViewport
import com.commonground.core.models.Coordinates
import com.commonground.core.models.Event
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.maplibre.compose.camera.CameraMoveReason
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun Map(
    modifier: Modifier,
    items: List<Event>,
    currentLocation: Coordinates?,
    onViewportChanged: (MapViewport) -> Unit
) {
    val cameraState = rememberCameraState()
    var selectedFeature by remember { mutableStateOf<Feature<Geometry, JsonObject?>?>(null) }
    var mapLoaded by remember { mutableStateOf(false) }
    val markerIcon = rememberVectorPainter(Icons.Default.Place)
    val geoJsonData = remember(items) {
        GeoJsonData.Features(
            FeatureCollection(
                features = items.filter { it.coordinates != null }.map { event ->
                    Feature(
                        geometry = Point(Position(longitude = event.coordinates!!.longitude, latitude = event.coordinates!!.latitude)),
                        properties = buildJsonObject {
                            put("id", event.id)
                            put("title", event.title)
                        }
                    )
                }
            )
        )
    }

    LaunchedEffect(selectedFeature) {
        println(selectedFeature)
    }

    LaunchedEffect(currentLocation, mapLoaded) {
        if (!mapLoaded || currentLocation == null) return@LaunchedEffect
        cameraState.animateTo(
            finalPosition = CameraPosition(
                target = Position(longitude = currentLocation.longitude, latitude = currentLocation.latitude),
                zoom = 6.0
            ),
            duration = 3.seconds
        )
    }

    LaunchedEffect(cameraState) {
        cameraState.awaitProjection()
        snapshotFlow { Pair(cameraState.moveReason, cameraState.position) }
            .filter { it.first == CameraMoveReason.GESTURE }
            .distinctUntilChanged()
            // Wait for 200ms of no camera position changes to confirm it finished moving (handles drags + inertia)
            .debounce(200.milliseconds)
            .mapNotNull { cameraState.projection?.queryEventViewport(cameraState.position.target) }
            .distinctUntilChanged()
            .collect { onViewportChanged(it) }
    }

    MaplibreMap(
        modifier =  modifier,
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/${if (isSystemInDarkTheme()) "fiord" else "liberty"}"),
        cameraState = cameraState,
        options = MapOptions(
            ornamentOptions = OrnamentOptions.AllEnabled
        ),
        onMapLoadFinished = { mapLoaded = true }
    ) {
        if (platform != Platform.JVM) { // TODO
            SymbolLayer(
                id = "events-layer",
                source = rememberGeoJsonSource(geoJsonData),
                iconImage = image(markerIcon),
                onClick = { features ->
                    selectedFeature = features.firstOrNull()
                    ClickResult.Consume
                }
            )
        }
    }

}
