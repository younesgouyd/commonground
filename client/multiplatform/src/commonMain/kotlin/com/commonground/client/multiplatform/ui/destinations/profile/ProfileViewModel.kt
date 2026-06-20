package com.commonground.client.multiplatform.ui.destinations.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.client.multiplatform.data.repositories.UserRepo
import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.core.models.User
import com.commonground.core.models.UserEventType
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepo: UserRepo,
    private val eventRepo: EventRepo,
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
            val profile = userRepo.getLoggedInUser()
            if (profile == null) {
                ProfileState.Error
            } else {
                val followers = MutableStateFlow(getFollowers())
                val followees = MutableStateFlow(getFollowees())
                ProfileState.Loaded(
                    user = profile,
                    events = ProfileState.Loaded.Events(
                        created = LazyList(
                            coroutineScope = viewModelScope,
                            load = { pageNumber ->
                                val events = eventRepo.getLoggedInUserEvents(UserEventType.Created, pageNumber)
                                LazyList.Chunk(
                                    items = events.items,
                                    next = events.next,
                                    totalCount = events.total
                                )
                            }
                        ),
                        attending = LazyList(
                            coroutineScope = viewModelScope,
                            load = { pageNumber ->
                                val events = eventRepo.getLoggedInUserEvents(UserEventType.Attending, pageNumber)
                                LazyList.Chunk(
                                    items = events.items,
                                    next = events.next,
                                    totalCount = events.total
                                )
                            }
                        ),
                        went = LazyList(
                            coroutineScope = viewModelScope,
                            load = { pageNumber ->
                                val events = eventRepo.getLoggedInUserEvents(UserEventType.Went, pageNumber)
                                LazyList.Chunk(
                                    items = events.items,
                                    next = events.next,
                                    totalCount = events.total
                                )
                            }
                        )
                    ),
                    follows = ProfileState.Loaded.Follows(
                        followers = followers.asStateFlow(),
                        following = followees.asStateFlow(),
                        onFollowUserClick = { userId ->
                            viewModelScope.launch { userRepo.followUser(userId) }.join()

                            // TODO: we're reloading the entire list just to reflect the follow state of one item. find a better way
                            followers.value = getFollowers()
                            followees.value = getFollowees()
                        },
                        onUnfollowUserClick = { userId ->
                            viewModelScope.launch { userRepo.unfollowUser(userId) }.join()

                            // TODO: we're reloading the entire list just to reflect the follow state of one item. find a better way
                            followees.value = getFollowees()
                            followers.value = getFollowers()
                        }
                    ),
                    onEditProfile = onEditProfile,
                    onToSettings = onToSettings,
                )
            }
        } catch (e: Exception) {
            logger.error(e) {  }
            ProfileState.Error
        }
    }

    private fun getFollowers(): LazyList<User> {
        return LazyList(
            coroutineScope = viewModelScope,
            load = { pageNumber ->
                val users = userRepo.getLoggedInUserFollowers(pageNumber)
                LazyList.Chunk(users.items, users.next, users.total)
            }
        )
    }

    private fun getFollowees(): LazyList<User> {
        return LazyList(
            coroutineScope = viewModelScope,
            load = { pageNumber ->
                val users = userRepo.getLoggedInUserFollowees(pageNumber)
                LazyList.Chunk(users.items, users.next, users.total)
            }
        )
    }
}
