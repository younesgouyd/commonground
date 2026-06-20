package com.commonground.client.multiplatform.ui.destinations.profile

import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.core.models.Event
import com.commonground.core.models.User
import kotlinx.coroutines.flow.StateFlow

sealed class ProfileState {
    data object Loading : ProfileState()

    data class Loaded(
        val user: User,
        val follows: Follows,
        val events: Events,
        val onEditProfile: () -> Unit,
        val onToSettings: () -> Unit
    ) : ProfileState() {
        data class Follows(
            val followers: StateFlow<LazyList<User>>,
            val following: StateFlow<LazyList<User>>,
            val onFollowUserClick: suspend (userId: String) -> Unit,
            val onUnfollowUserClick: suspend (userId: String) -> Unit
        )
        data class Events(
            val created: LazyList<Event>,
            val attending: LazyList<Event>,
            val went: LazyList<Event>
        )
    }

    data object Error : ProfileState()
}