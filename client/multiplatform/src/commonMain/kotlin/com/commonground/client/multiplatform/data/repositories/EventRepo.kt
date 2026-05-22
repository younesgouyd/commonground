package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.Event
import com.commonground.core.models.UserEvents
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class EventRepo(
    private val client: HttpClient
) {
    // TODO: implement pagination
    suspend fun getHomePageEvents(): List<Event> {
        return client.get("events").body<List<Event>>()
    }

    suspend fun getUserEvents(): UserEvents {
        TODO()
    }
}