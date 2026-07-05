package com.commonground.client.multiplatform.data.repositories

import com.commonground.client.multiplatform.data.HttpClientHolder
import com.commonground.core.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class EventRepo(
    private val holder: HttpClientHolder
) {
    private val client get() = holder.client
    suspend fun getNearbyEvents(
        latitude: Double,
        longitude: Double,
        radiusKilometers: Int,
        isPrivate: Boolean?,
        isPrivatePlace: Boolean?,
        isPaid: Boolean?,
        title: String?,
        pageNumber: Int
    ): Events {
        return client.get("events") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("radiusKilometers", radiusKilometers)
            parameter("isPrivate", isPrivate)
            parameter("isPrivatePlace", isPrivatePlace)
            parameter("isPaid", isPaid)
            parameter("title", title?.trim()?.ifBlank { null })
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

    suspend fun getEventAttendees(
        eventId: String,
        pageNumber: Int
    ): Users {
        return client.get("events/$eventId/attendees") {
            parameter("pageNumber", pageNumber)
        }.body<Users>()
    }

    suspend fun bookEvent(eventId: String) {
        client.post("events/$eventId/book")
    }

    suspend fun unbookEvent(eventId: String) {
        client.delete("events/$eventId/book")
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