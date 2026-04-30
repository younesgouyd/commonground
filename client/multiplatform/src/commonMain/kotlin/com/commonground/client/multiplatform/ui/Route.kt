package com.commonground.client.multiplatform.ui

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable data object Home : Route()
    @Serializable data object Me : Route()
    @Serializable data object Friends : Route()
    @Serializable data object Settings : Route()
    @Serializable data class Event(val id: String) : Route()
    @Serializable data class User(val id: String) : Route()
}
