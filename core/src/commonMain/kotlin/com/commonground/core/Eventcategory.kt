package com.commonground.core

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CategoryId(val value: String)

@Serializable
data class EventCategory(
    val id: CategoryId,
    val name: String,
    val description: String? = null,
    val iconKey: String? = null
)

