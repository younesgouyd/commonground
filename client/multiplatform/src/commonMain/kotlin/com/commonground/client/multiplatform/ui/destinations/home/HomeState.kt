package com.commonground.client.multiplatform.ui.destinations.home

import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.client.multiplatform.ui.MapViewport
import com.commonground.core.models.Coordinates
import com.commonground.core.models.Event

sealed class HomeState {
    data object Loading : HomeState()

    data class Loaded(
        val events: LazyList<Event>,
        val currentLocation: Coordinates?,
        val onMapViewportChanged: (MapViewport) -> Unit
    ) : HomeState()

    data object Error : HomeState()
}