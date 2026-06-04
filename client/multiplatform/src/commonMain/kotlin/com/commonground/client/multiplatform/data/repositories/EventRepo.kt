package com.commonground.client.multiplatform.data.repositories

import com.commonground.client.multiplatform.data.LocationManager
import com.commonground.core.models.Events
import com.commonground.core.models.UserEvents
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class EventRepo(
    private val client: HttpClient
) {
    suspend fun getNearbyEvents(pageNumber: Int): Events {
        val coordinates = LocationManager.getCurrentLocation()
        return if (coordinates != null) {
            client.get("events") {
                parameter("latitude", coordinates.latitude)
                parameter("longitude", coordinates.longitude)
                parameter("radiusKilometers", 500)
                parameter("pageNumber", pageNumber)
            }.body<Events>()
        } else Events(emptyList(), null)
    }

    suspend fun getUserEvents(): UserEvents {
        TODO()
    }
}