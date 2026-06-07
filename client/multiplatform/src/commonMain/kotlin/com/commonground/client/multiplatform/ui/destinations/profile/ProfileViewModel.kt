package com.commonground.client.multiplatform.ui.destinations.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.UserRepo
import com.commonground.core.models.Event
import com.commonground.core.models.User
import com.commonground.core.models.UserEvents
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    data object Loading : ProfileState()

    data class Loaded(
        val user: User,
        val events: UserEvents,
        val friends: List<Friend>,
        val friendCount: Int,
        val eventCount: Int,
        val onEditProfile: () -> Unit,
        val onToSettings: () -> Unit,
    ) : ProfileState() {
        data class Friend(
            val id: String,
            val username: String,
            val displayName: String?,
        )
    }

    data object Error : ProfileState()
}

class ProfileViewModel(
    private val userRepo: UserRepo,
    private val onEditProfile: () -> Unit = {},
    private val onToSettings: () -> Unit = {},
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    private val _state: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                // TODO: GET (/api/v1/user/me)
                val currentUser = User(
                    id = "1",
                    username = "Tarik",
                    displayName = "itsmetarikov",
                    bio = "LLLLLLLLLLoL",
                    profilePic = null
                )
                val friends = mutableListOf<ProfileState.Loaded.Friend>()
                for (i in 1..10) {
                    friends.add(
                        ProfileState.Loaded.Friend("$i", "Friend $i", "Friend $i"),
                    )
                }

                val createdEvents = listOf(
                    Event(
                        id = "e1",
                        title = "Casa Chess Tournament",
                        description = "LOL",
                        locationName = "LOL",
                        coordinates = null,
                        date = "LOL",
                        isPrivate = false,
                        durationMinutes = 240,
                        isPaid = false,
                        creator = currentUser
                    ),
                )

                val goingEvents = listOf(
                    Event(
                        id = "e2",
                        title = "Casa Chess Tournament",
                        description = "LOL",
                        locationName = "LOL",
                        coordinates = null,
                        date = "LOL",
                        isPrivate = false,
                        durationMinutes = 240,
                        isPaid = false,
                        creator = currentUser
                    ),
                )

                val wentEvents = mutableListOf<Event>()

                for (i in 1..20) {
                    wentEvents.add(
                        Event(
                            id = "e$i",
                            title = "Casa Chess Tournament $i",
                            description = "LOL",
                            locationName = "LOL",
                            coordinates = null,
                            date = "LOL",
                            isPrivate = false,
                            durationMinutes = 240,
                            isPaid = false,
                            creator = currentUser
                        )
                    )
                }

                ProfileState.Loaded(
                    user = currentUser,
                    events = UserEvents(
                        created = createdEvents,
                        going = goingEvents,
                        went = wentEvents
                    ),
                    friends = friends,
                    friendCount = friends.size,
                    eventCount = createdEvents.size + goingEvents.size + wentEvents.size,
                    onEditProfile = onEditProfile,
                    onToSettings = onToSettings,
                )
            } catch (e: Exception) {
                logger.error(e) {}
                ProfileState.Error
            }
        }
    }
}
