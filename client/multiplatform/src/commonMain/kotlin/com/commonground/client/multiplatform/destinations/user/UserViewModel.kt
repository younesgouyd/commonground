package com.commonground.client.multiplatform.destinations.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.core.Event
import com.commonground.core.EventId
import com.commonground.core.ImageUrl
import com.commonground.core.User
import com.commonground.core.UserId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

sealed class UserState {
    data object Loading : UserState()

    data class Loaded(
        val user: User,
        val friends: List<Friend>,
        val events: Events,
        val friendState: FriendState,
        val followState: FollowState,
        val onChangeFollowState: (FollowState) -> Unit,
    ) : UserState() {
        data class Friend(
            val id: UserId,
            val username: String,
            val displayName: String?,
            val profilePic: ImageUrl?
        )

        data class Events(
            val created: List<Event>,
            val going: List<Event>,
            val went: List<Event>
        )

        sealed class FriendState {
            data class Friend(val onRemoveClick: () -> Unit) : FriendState()
            data class NonFriend(val onSendRequestClick: () -> Unit) : FriendState()
        }

        enum class FollowState {
            Followed, FollowedWithNotifications, Unfollowed
        }
    }

    data object NotFound : UserState()
}

class UserViewModel(
    val id: UserId
) : ViewModel() {
    val state get() = _state.asStateFlow()
    private val _state: MutableStateFlow<UserState> = MutableStateFlow(UserState.Loading)

    init {
        viewModelScope.launch {
            _state.value = UserState.Loaded(
                user = User(
                    id = id,
                    username = "morpheus",
                    displayName = "Morpheus",
                    profilePic = null
                ),
                friends = listOf(
                    UserState.Loaded.Friend(UserId("2"), "trinity", "Trinity", null),
                    UserState.Loaded.Friend(UserId("3"), "neo", "Thomas A. Anderson", null)
                ),
                events = UserState.Loaded.Events(
                    created = listOf(
                        Event(EventId("1"), "Chess Tournament", "A competitive open-bracket chess tournament.", "Central Park", "2026-05-15", false, 5.hours, false)
                    ),
                    going = listOf(
                        Event(EventId("2"), "Tech Meetup", "Developers discussing Kotlin Multiplatform.", "Tech Hub Office", "2026-05-20", true, 3.hours, false),
                    ),
                    went = emptyList()
                ),
                friendState = UserState.Loaded.FriendState.NonFriend(onSendRequestClick = {
                    println("Request sent to $id")
                }),
                followState = UserState.Loaded.FollowState.Unfollowed,
                onChangeFollowState = { newState ->
                    println("Follow state changed to: $newState")
                }
            )
        }
    }
}