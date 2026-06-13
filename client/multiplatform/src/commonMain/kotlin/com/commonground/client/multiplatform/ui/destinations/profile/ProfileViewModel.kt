package com.commonground.client.multiplatform.ui.destinations.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.UserRepo
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
            _state.value = loadProfile()
        }
    }

    private suspend fun loadProfile(): ProfileState {
        return try {
            val profile = userRepo.getMyProfile()
            ProfileState.Loaded(
                user = profile.user,
                events = profile.events,
                friends = emptyList(), // TODO: getFriends end point not Impl yt
                friendCount = profile.friendCount,
                eventCount = profile.eventCount,
                onEditProfile = onEditProfile,
                onToSettings = onToSettings,
            )
        } catch (e: Exception) {
            logger.error(e) {  }
            ProfileState.Error
        }
    }
}
