package com.commonground.client.multiplatform

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable data object Profile : Route()
    @Serializable data object Home : Route()
    @Serializable data object Friends : Route()
    @Serializable data object Settings : Route()
    @Serializable data class Event(val id: Long) : Route()
    @Serializable data class User(val id: Long) : Route()
}
