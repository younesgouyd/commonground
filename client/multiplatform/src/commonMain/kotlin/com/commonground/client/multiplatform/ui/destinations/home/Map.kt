package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.commonground.client.multiplatform.Platform
import com.commonground.client.multiplatform.platform
import com.commonground.core.models.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraProjection
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
import org.maplibre.compose.util.VisibleRegion
import org.maplibre.spatialk.geojson.*
import kotlin.math.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun Map(
    modifier: Modifier,
    items: List<Event>,
    currentLocation: HomeState.Loaded.Coordinates?,
    onViewportChanged: (EventViewport) -> Unit
) {
    val cameraState = rememberCameraState()
    var selectedFeature by remember { mutableStateOf<Feature<Geometry, JsonObject?>?>(null) }
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

    LaunchedEffect(currentLocation) {
        if (currentLocation == null) return@LaunchedEffect
        delay(500.milliseconds)
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
        snapshotFlow { cameraState.isCameraMoving to cameraState.position }
            .filter { !it.first }
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
        )
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

private fun CameraProjection.queryEventViewport(center: Position): EventViewport {
    val visibleRegion = queryVisibleRegion()
    val radiusKilometers = visibleRegion.corners.maxOf { center.distanceKilometersTo(it) }
    return EventViewport(
        latitude = center.latitude,
        longitude = center.longitude,
        radiusKilometers = ceil(radiusKilometers).toInt().coerceAtLeast(1)
    )
}

private val VisibleRegion.corners: List<Position>
    get() = listOf(farLeft, farRight, nearLeft, nearRight)

private fun Position.distanceKilometersTo(other: Position): Double {
    val earthRadiusKilometers = 6371.0
    val latitudeDelta = (other.latitude - latitude).toRadians()
    val longitudeDelta = (other.longitude - longitude).toRadians()
    val startLatitude = latitude.toRadians()
    val endLatitude = other.latitude.toRadians()

    val a = (sin(latitudeDelta / 2) * sin(latitudeDelta / 2) +
            cos(startLatitude) * cos(endLatitude) *
            sin(longitudeDelta / 2) * sin(longitudeDelta / 2)).coerceIn(0.0, 1.0)
    return earthRadiusKilometers * 2 * asin(sqrt(a))
}

private fun Double.toRadians(): Double = this * PI / 180.0