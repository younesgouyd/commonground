package com.commonground.client.multiplatform.ui.destinations.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.core.models.Event
import com.commonground.core.models.ImageUrl
import com.commonground.core.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            val id: String,
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
    val id: String
) : ViewModel() {
    private val _state: MutableStateFlow<UserState> = MutableStateFlow(UserState.Loading)
    val state = _state.asStateFlow()

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
                    UserState.Loaded.Friend("2", "trinity", "Trinity", null),
                    UserState.Loaded.Friend("3", "neo", "Thomas A. Anderson", null)
                ),
                events = UserState.Loaded.Events(
                    created = emptyList(),
                    going = emptyList(),
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