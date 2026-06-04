package com.commonground.server.util

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

object GeometryUtils {
    private val geometryFactory: GeometryFactory by lazy { GeometryFactory(PrecisionModel(), 4326) }

    fun createPoint(latitude: Double, longitude: Double): Point {
        return geometryFactory.createPoint(Coordinate(longitude, latitude))
    }
}