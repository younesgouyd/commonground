package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.Event
import com.commonground.core.models.Events
import com.commonground.core.models.SaveEventRequest
import com.commonground.core.models.UserEventType
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

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

    suspend fun createEvent(request: SaveEventRequest): Event {
        return client.post("events") {
            setBody(request)
        }.body<Event>()
    }

    suspend fun updateEvent(id: String, request: SaveEventRequest) {
        client.patch("events/$id") {
            setBody(request)
        }
    }

    suspend fun updateImage(id: String, image: ByteArray) {
        client.patch("events/$id/image") {
            setBody(
                MultiPartFormDataContent(
                    parts = formData {
                        append(
                            key = "file",
                            value = image,
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"event.jpg\"")
                            }
                        )
                    }
                )
            )
        }
    }

    suspend fun clearImage(id: String) {
        client.delete("events/$id/image")
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
        return client.get("users/$id/events") {
            parameter("type", type)
            parameter("pageNumber", pageNumber)
        }.body<Events>()
    }
}