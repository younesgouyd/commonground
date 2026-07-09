package com.commonground.client.multiplatform.data.repositories

import com.commonground.client.multiplatform.data.HttpClientHolder
import com.commonground.core.models.EventCategory
import io.ktor.client.call.*
import io.ktor.client.request.*

class CategoryRepo(
    private val holder: HttpClientHolder
) {
    private val client get() = holder.client

    suspend fun getAllCategories(): List<EventCategory> {
        return try {
            client.get("categories").body<List<EventCategory>>()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getPreferredCategories(): Set<String> = emptySet()

    suspend fun savePreferredCategories(ids: Set<String>) { /* TODO: server endpoint */ }
}
