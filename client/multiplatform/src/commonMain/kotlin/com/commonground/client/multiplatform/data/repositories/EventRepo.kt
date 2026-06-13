package com.commonground.client.multiplatform.data.repositories

import com.commonground.client.multiplatform.data.LocationManager
import com.commonground.core.models.CreateEventRequest
import com.commonground.core.models.Event
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

    suspend fun getEvent(id: String): Event {
        return client.get("events/$id").body<Event>()
    }

    suspend fun createEvent(request: CreateEventRequest): Event {
        return client.post("events") {
            setBody(request)
        }.body<Event>()
    }

    suspend fun getUserEvents(): UserEvents {
        return client.get("user/events").body<UserEvents>()
    }
}