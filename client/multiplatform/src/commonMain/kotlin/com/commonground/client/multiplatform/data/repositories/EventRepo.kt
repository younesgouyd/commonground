package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.Event
import com.commonground.core.models.UserEvents
import io.ktor.client.*

class EventRepo(
    private val client: HttpClient
) {
    // TODO
    fun getHomePageEvents(): List<Event> {
        return emptyList()
    }

    suspend fun getUserEvents(): UserEvents {
        TODO()
    }
}