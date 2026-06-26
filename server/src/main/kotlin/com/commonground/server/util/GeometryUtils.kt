package com.commonground.server.util

import com.commonground.core.models.Coordinates
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

object GeometryUtils {
    private val geometryFactory: GeometryFactory by lazy { GeometryFactory(PrecisionModel(), 4326) }

    fun createPoint(latitude: Double, longitude: Double): Point {
        return geometryFactory.createPoint(Coordinate(longitude, latitude))
    }

    fun Coordinates.toPoint(): Point {
        return geometryFactory.createPoint(Coordinate(this.longitude, this.latitude))
    }

    fun fromCoordinates(coordinates: Coordinates): Point {
        return geometryFactory.createPoint(Coordinate(coordinates.longitude, coordinates.latitude))
    }
}