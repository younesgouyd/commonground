package com.commonground.client.multiplatform.ui

import org.maplibre.compose.camera.CameraProjection
import org.maplibre.compose.util.VisibleRegion
import org.maplibre.spatialk.geojson.Position
import kotlin.math.*

data class MapViewport(
    val latitude: Double,
    val longitude: Double,
    val radiusKilometers: Int
)

fun CameraProjection.queryEventViewport(center: Position): MapViewport {
    val visibleRegion = queryVisibleRegion()
    val radiusKilometers = visibleRegion.corners.maxOf { center.distanceKilometersTo(it) }
    return MapViewport(
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