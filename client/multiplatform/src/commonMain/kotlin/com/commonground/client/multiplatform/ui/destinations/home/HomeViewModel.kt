package com.commonground.client.multiplatform.ui.destinations.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.core.Event
import com.commonground.core.EventId
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

sealed class HomeState {
    data object Loading : HomeState()

    data class Loaded(
        val events: List<Event>
    ) : HomeState()

    data object Error : HomeState()
}

class HomeViewModel(
    private val eventRepo: EventRepo
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                HomeState.Loaded(
                    events = eventRepo.getHomePageEvents()
                )
            } catch (e: Exception) {
                logger.error(e) {  }
                HomeState.Error
            }
        }
    }

    fun search(query: String) {
        // TODO
    }
}