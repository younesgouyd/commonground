package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.Event
import com.commonground.core.UserEvents
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