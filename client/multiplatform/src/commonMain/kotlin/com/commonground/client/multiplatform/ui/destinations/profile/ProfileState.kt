package com.commonground.client.multiplatform.ui.destinations.profile

import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.core.models.Event
import com.commonground.core.models.User

sealed class ProfileState {
    data object Loading : ProfileState()

    data class Loaded(
        val user: User,
        val friends: List<Friend>,
        val friendCount: Int,
//        val follows: Follows,
        val events: Events,
        val onEditProfile: () -> Unit,
        val onToSettings: () -> Unit,
    ) : ProfileState() {
        data class Friend(
            val id: String,
            val username: String,
            val displayName: String?,
        )
//        data class Follows(
//            val followers: LazyList<User>,
//            val following: LazyList<User>
//        )
        data class Events(
            val created: LazyList<Event>,
            val attending: LazyList<Event>,
            val went: LazyList<Event>
        )
    }

    data object Error : ProfileState()
}