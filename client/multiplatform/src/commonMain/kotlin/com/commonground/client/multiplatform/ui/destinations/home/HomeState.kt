package com.commonground.client.multiplatform.ui.destinations.home

import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.client.multiplatform.ui.MapViewport
import com.commonground.core.models.Event
import kotlinx.coroutines.flow.StateFlow

sealed class HomeState {
    data object Loading : HomeState()

    data class Loaded(
        val searchRequest: StateFlow<SearchRequest>,
        val events: StateFlow<LazyList<Event>>,
        val onSearchChange: (SearchRequest) -> Unit
    ) : HomeState() {
        data class SearchRequest(
            val mapViewport: MapViewport?,
            val isPrivate: Boolean?,
            val isPrivatePlace: Boolean?,
            val isPaid: Boolean?,
            val title: String
        )
    }

    data object Error : HomeState()
}