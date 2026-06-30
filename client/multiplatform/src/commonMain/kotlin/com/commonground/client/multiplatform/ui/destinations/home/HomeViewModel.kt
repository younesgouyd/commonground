package com.commonground.client.multiplatform.ui.destinations.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.LocationManager
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.client.multiplatform.ui.MapViewport
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val eventRepo: EventRepo
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.Loading)
    private val searchRequest: MutableStateFlow<HomeState.Loaded.SearchRequest> = MutableStateFlow(
        HomeState.Loaded.SearchRequest(
            mapViewport = null,
            isPrivate = null,
            isPrivatePlace = null,
            isPaid = null,
            title = ""
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            searchRequest.update {
                it.copy(mapViewport = LocationManager.getCurrentLocation()?.let { MapViewport(it.latitude, it.longitude, 500) })
            }
            _state.value = try {
                HomeState.Loaded(
                    searchRequest = searchRequest.asStateFlow(),
                    events = searchRequest.mapLatest { search ->
                        LazyList(
                            coroutineScope = viewModelScope,
                            load = { pageNumber ->
                                if (search.mapViewport == null) {
                                    LazyList.Chunk(emptyList(), null, null)
                                } else {
                                    val events = eventRepo.getNearbyEvents(
                                        latitude = search.mapViewport.latitude,
                                        longitude = search.mapViewport.longitude,
                                        radiusKilometers = search.mapViewport.radiusKilometers.coerceAtLeast(1),
                                        isPrivate = search.isPrivate,
                                        isPrivatePlace = search.isPrivatePlace,
                                        isPaid = search.isPaid,
                                        title = search.title,
                                        pageNumber = pageNumber,
                                    )
                                    LazyList.Chunk(
                                        items = events.items,
                                        next = events.next,
                                        totalCount = events.total
                                    )
                                }
                            }
                        )
                    }.stateIn(viewModelScope),
                    onSearchChange = { searchRequest.value = it }
                )
            } catch (e: Exception) {
                logger.error(e) {  }
                HomeState.Error
            }
        }
    }
}
