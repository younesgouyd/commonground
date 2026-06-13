package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.Events
import com.commonground.core.models.UserEvents
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class EventRepo(
    private val client: HttpClient
) {
    suspend fun getNearbyEvents(
        latitude: Double,
        longitude: Double,
        radiusKilometers: Int,
        pageNumber: Int
    ): Events {
        return client.get("events") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("radiusKilometers", radiusKilometers)
            parameter("pageNumber", pageNumber)
        }.body<Events>()
    }

    suspend fun getUserEvents(): UserEvents {
        TODO()
    }
}
