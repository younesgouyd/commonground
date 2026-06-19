package com.commonground.client.multiplatform.ui.destinations.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.widgets.*

interface UserNavActions {
    fun toUser(id: String)
    fun toEvent(id: String)
}

@Composable
fun User(
    viewModel: UserViewModel,
    navActions: UserNavActions
) {
    val state by viewModel.state.collectAsState()

    AdaptiveUi(
        wide = { Wide(state, navActions) },
        compact = { Compact(state, navActions) }
    )
}


@Composable
private fun Wide(
    state: UserState,
    navActions: UserNavActions
) {
    when (state) {
        is UserState.Loading -> Text("Loading...")
        is UserState.Loaded -> Wide(state, navActions)
        is UserState.NotFound -> Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
    }
}

private enum class Tabs { Events, Friends }

@Composable
private fun Wide(
    state: UserState.Loaded,
    navActions: UserNavActions
) {
    val tabs = remember { Tabs.entries }
    var selectedTabIndex by remember { mutableStateOf(Pair(0, tabs.first())) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            columns = GridCells.Adaptive(200.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ItemDetailsHeaderWide(
                    modifier = Modifier.fillMaxWidth().height(500.dp),
                    title = state.user.displayName ?: state.user.username,
                    image = Image.ImageVector(Icons.Default.Person), // TODO
                    mainAction = when (state.friendState) {
                        is UserState.Loaded.FriendState.Friend -> MainHeaderAction(
                            label = "Unfriend",
                            icon = Icons.Default.PersonRemove,
                            onClick = state.friendState.onRemoveClick
                        )

                        is UserState.Loaded.FriendState.NonFriend -> MainHeaderAction(
                            label = "Send friend request",
                            icon = Icons.Default.PersonAdd,
                            onClick = state.friendState.onSendRequestClick
                        )
                    },
                    actions = listOf(
                        HeaderAction.DropDown(
                            label = "Follow",
                            icon = Icons.Default.PersonAddAlt,
                            options = listOf(
                                DropdownOption("Followed", UserState.Loaded.FollowState.Followed),
                                DropdownOption(
                                    "Followed with notifications",
                                    UserState.Loaded.FollowState.FollowedWithNotifications
                                ),
                                DropdownOption("Unfollowed", UserState.Loaded.FollowState.Unfollowed),
                            ),
                            selectedOption = state.followState,
                            onChange = { state.onChangeFollowState(it as UserState.Loaded.FollowState) }
                        )
                    )
                )
            }
            item(span = { GridItemSpan(maxLineSpan)}) {
                PrimaryTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedTabIndex.first
                ) {
                    tabs.forEachIndexed { index, item ->
                        Tab(
                            text = { Text(item.name) },
                            selected = false,
                            onClick = { selectedTabIndex = Pair(index, item) }
                        )
                    }
                }
            }
            when (selectedTabIndex.second) {
                Tabs.Events -> items(state.events.created) { event ->
                    EventCard(
                        event = event,
                        onClick = { navActions.toEvent(event.id) },
                        onUserClick = {}
                    )
                }
                Tabs.Friends -> Unit
            }
        }
    }
}

@Composable
private fun Compact(
    state: UserState,
    navActions: UserNavActions
) {

}