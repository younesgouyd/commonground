package com.commonground.client.multiplatform.ui.destinations.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.LocationManager
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.client.multiplatform.ui.MapViewport
import com.commonground.core.models.Event
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val eventRepo: EventRepo
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.Loading)
    private var viewport: MapViewport? = null

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val currentLocation = LocationManager.getCurrentLocation()
            _state.value = try {
                viewport = currentLocation?.let { initialViewport(it.latitude, it.longitude) }
                HomeState.Loaded(
                    events = createEventsList(viewport),
                    currentLocation = currentLocation,
                    onMapViewportChanged = ::setMapViewport
                )
            } catch (e: Exception) {
                logger.error(e) {  }
                HomeState.Error
            }
        }
    }

    private fun setMapViewport(viewport: MapViewport) {
        val loaded = _state.value as? HomeState.Loaded ?: return
        if (this.viewport == viewport) return

        _state.value = loaded.copy(
            events = createEventsList(viewport)
        )
    }

    fun search(query: String) {
        // TODO
    }

    private fun initialViewport(latitude: Double, longitude: Double): MapViewport {
        return MapViewport(
            latitude = latitude,
            longitude = longitude,
            radiusKilometers = 500
        )
    }

    private fun createEventsList(viewport: MapViewport?): LazyList<Event> {
        return LazyList(
            coroutineScope = viewModelScope,
            load = { pageNumber ->
                if (viewport == null) {
                    LazyList.Chunk(emptyList(), null, null)
                } else {
                    val events = eventRepo.getNearbyEvents(
                        latitude = viewport.latitude,
                        longitude = viewport.longitude,
                        radiusKilometers = viewport.radiusKilometers.coerceAtLeast(1),
                        pageNumber = pageNumber
                    )
                    LazyList.Chunk(
                        items = events.items,
                        next = events.next,
                        totalCount = events.total
                    )
                }
            }
        )
    }
}
