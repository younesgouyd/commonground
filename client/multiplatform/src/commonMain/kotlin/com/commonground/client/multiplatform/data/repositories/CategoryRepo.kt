package com.commonground.client.multiplatform.data.repositories;

import com.commonground.core.EventCategory

interface CategoryRepo {
    suspend fun getAllCategories(): List<EventCategory>
    suspend fun getPreferredCategories(): Set<String>
    suspend fun savePreferredCategories(ids: Set<String>)
}
 
