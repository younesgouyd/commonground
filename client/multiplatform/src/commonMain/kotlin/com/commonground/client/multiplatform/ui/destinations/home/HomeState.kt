package com.commonground.client.multiplatform.ui.destinations.home

import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.core.models.Event

sealed class HomeState {
    data object Loading : HomeState()

    data class Loaded(
        val events: LazyList<Event>,
        val currentLocation: Coordinates?,
        val onMapViewportChanged: (EventViewport) -> Unit
    ) : HomeState() {
        data class Coordinates(val latitude: Double, val longitude: Double)
    }

    data object Error : HomeState()
}