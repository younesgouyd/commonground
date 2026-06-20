package com.commonground.client.multiplatform.ui.destinations.user

import com.commonground.client.multiplatform.ui.widgets.Events
import com.commonground.client.multiplatform.ui.widgets.Follows
import com.commonground.core.models.User
import kotlinx.coroutines.flow.StateFlow

sealed class UserState {
    data object Loading : UserState()

    data class Loaded(
        val user: StateFlow<User>,
        val follows: Follows,
        val events: Events
    ) : UserState()

    data object Error : UserState()
}