package com.commonground.client.multiplatform.ui.destinations.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.client.multiplatform.data.repositories.UserRepo
import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.client.multiplatform.ui.widgets.Events
import com.commonground.client.multiplatform.ui.widgets.Follows
import com.commonground.core.models.User
import com.commonground.core.models.UserEventType
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    val id: String,
    private val userRepo: UserRepo,
    private val eventRepo: EventRepo
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    private val _state: MutableStateFlow<UserState> = MutableStateFlow(UserState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = loadProfile()
        }
    }

    private suspend fun loadProfile(): UserState {
        return try {
            val user = userRepo.getUser(id)
            if (user == null) {
                UserState.Error
            } else {
                val user = MutableStateFlow(user)
                val followers = MutableStateFlow(getFollowers())
                val followees = MutableStateFlow(getFollowees())
                UserState.Loaded(
                    user = user.asStateFlow(),
                    events = Events(
                        created = LazyList(
                            coroutineScope = viewModelScope,
                            load = { pageNumber ->
                                val events = eventRepo.getUserEvents(id, UserEventType.Created, pageNumber)
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
                                val events = eventRepo.getUserEvents(id, UserEventType.Attending, pageNumber)
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
                                val events = eventRepo.getUserEvents(id, UserEventType.Went, pageNumber)
                                LazyList.Chunk(
                                    items = events.items,
                                    next = events.next,
                                    totalCount = events.total
                                )
                            }
                        )
                    ),
                    follows = Follows(
                        followers = followers.asStateFlow(),
                        following = followees.asStateFlow(),
                        onFollowUserClick = { userId ->
                            viewModelScope.launch { userRepo.followUser(userId) }.join()
                            refreshStateAfterFollowOperation(userId, user, followers, followees)
                        },
                        onUnfollowUserClick = { userId ->
                            viewModelScope.launch { userRepo.unfollowUser(userId) }.join()
                            refreshStateAfterFollowOperation(userId, user, followers, followees)
                        }
                    )
                )
            }
        } catch (e: Exception) {
            logger.error(e) {  }
            UserState.Error
        }
    }

    private suspend fun refreshStateAfterFollowOperation(
        userId: String,
        user: MutableStateFlow<User>,
        followers: MutableStateFlow<LazyList<User>>,
        followees: MutableStateFlow<LazyList<User>>
    ) {
        if (userId == id) {
            val temp = userRepo.getUser(id)
            if (temp == null) {
                _state.value = UserState.Error
            } else {
                user.value = temp
            }
        } else {
            // TODO: we're reloading the entire list just to reflect the follow state of one item. find a better way
            followers.value = getFollowers()
            followees.value = getFollowees()
        }
    }

    private fun getFollowers(): LazyList<User> {
        return LazyList(
            coroutineScope = viewModelScope,
            load = { pageNumber ->
                val users = userRepo.getUserFollowers(id, pageNumber)
                LazyList.Chunk(users.items, users.next, users.total)
            }
        )
    }

    private fun getFollowees(): LazyList<User> {
        return LazyList(
            coroutineScope = viewModelScope,
            load = { pageNumber ->
                val users = userRepo.getUserFollowees(id, pageNumber)
                LazyList.Chunk(users.items, users.next, users.total)
            }
        )
    }
}