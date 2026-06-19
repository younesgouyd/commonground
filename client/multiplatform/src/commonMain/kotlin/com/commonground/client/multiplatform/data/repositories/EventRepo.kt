package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.CreateEventRequest
import com.commonground.core.models.Event
import com.commonground.core.models.Events
import com.commonground.core.models.UserEventType
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

    suspend fun getEvent(id: String): Event? {
        return client.get("events/$id").body<Event?>()
    }

    suspend fun createEvent(request: CreateEventRequest): Event {
        return client.post("events") {
            setBody(request)
        }.body<Event>()
    }

    suspend fun getLoggedInUserEvents(
        type: UserEventType,
        pageNumber: Int
    ): Events {
        return client.get("me/events") {
            parameter("type", type)
            parameter("pageNumber", pageNumber)
        }.body<Events>()
    }

    suspend fun getUserEvents(
        id: String,
        type: UserEventType,
        pageNumber: Int
    ): Events {
        return client.get("user/events") {
            parameter("id", id)
            parameter("type", type)
            parameter("pageNumber", pageNumber)
        }.body<Events>()
    }
}