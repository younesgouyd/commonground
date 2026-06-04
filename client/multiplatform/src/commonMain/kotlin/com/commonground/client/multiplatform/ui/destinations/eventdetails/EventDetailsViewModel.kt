package com.commonground.client.multiplatform.ui.destinations.eventdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.core.models.Event
import com.commonground.core.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EventDetailsState {
    data object Loading : EventDetailsState()

    data class Loaded(
        val event: Event,
        val creators: List<User>
    ) : EventDetailsState()

    data object NotFound : EventDetailsState()
}

class EventDetailsViewModel(
    val id: String
) : ViewModel() {
    private val _state: MutableStateFlow<EventDetailsState> = MutableStateFlow(EventDetailsState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = EventDetailsState.Loaded(
                event = Event(
                    "1",
                    "Chess Tournament",
                    "A competitive open-bracket chess tournament.",
                    "Central Park",
                    Event.Coordinates(33.538667,-7.685083),
                    "2026-05-15",
                    false,
                    5 * 60,
                    false,
                    creator = User(
                        id = "111",
                        username = "neo",
                        displayName = "Neo",
                        bio = "Developing CommonGround",
                        emailAddress = "neo@example.com",
                        profilePic = null
                    )
                ),
                creators = listOf(
                    User(
                        id = "222",
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