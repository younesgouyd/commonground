package com.commonground.client.multiplatform.ui.destinations.eventdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.core.Event
import com.commonground.core.EventId
import com.commonground.core.User
import com.commonground.core.UserId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

sealed class EventDetailsState {
    data object Loading : EventDetailsState()

    data class Loaded(
        val event: Event,
        val creators: List<User>
    ) : EventDetailsState()

    data object NotFound : EventDetailsState()
}

class EventDetailsViewModel(
    val id: EventId
) : ViewModel() {
    private val _state: MutableStateFlow<EventDetailsState> = MutableStateFlow(EventDetailsState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = EventDetailsState.Loaded(
                event = Event(
                    EventId("1"),
                    "Chess Tournament",
                    "A competitive open-bracket chess tournament.",
                    "Central Park",
                    "2026-05-15",
                    false,
                    5 * 60,
                    false
                ),
                creators = listOf(
                    User(
                        id = UserId("1"),
                        username = "user1",
                        displayName = "John Doe",
                        emailAddress = "john.doe@example.com",
                        profilePic = null
                    )
                )
            )
        }
    }
}