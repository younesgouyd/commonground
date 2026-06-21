package com.commonground.client.multiplatform.ui.destinations.profile

import com.commonground.client.multiplatform.ui.widgets.Events
import com.commonground.client.multiplatform.ui.widgets.Follows
import com.commonground.core.models.User
import kotlinx.coroutines.flow.StateFlow

sealed class ProfileState {
    data object Loading : ProfileState()

    data class Loaded(
        val user: StateFlow<User>,
        val follows: Follows,
        val events: Events,
        val onUpdateProfilePic: (ByteArray) -> Unit,
        val onUpdateProfile: suspend (username: String, displayName: String?, bio: String?) -> Unit
    ) : ProfileState()

    data object Error : ProfileState()
}