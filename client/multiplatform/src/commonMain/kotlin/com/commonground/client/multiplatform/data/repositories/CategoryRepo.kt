package com.commonground.client.multiplatform.data.repositories;


import com.commonground.core.CategoryId
import com.commonground.core.EventCategory
 
interface CategoryRepo {
    suspend fun getAllCategories(): List<EventCategory>
    suspend fun getPreferredCategories(): Set<CategoryId>
    suspend fun savePreferredCategories(ids: Set<CategoryId>)
}
 
