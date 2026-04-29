package com.commonground.client.multiplatform.destinations.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.AdaptiveUi
import com.commonground.client.multiplatform.widgets.*
import com.commonground.core.Event
import com.commonground.core.EventId
import com.commonground.core.UserId

interface UserNavActions {
    fun toUser(id: UserId)
    fun toEvent(id: EventId)
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
                    Event(
                        event = event,
                        onClick = { navActions.toEvent(event.id) }
                    )
                }
                Tabs.Friends -> Unit
            }
        }
    }
}

@Composable
private fun Event(
    event: Event,
    onClick: () -> Unit
) {
    Card(onClick) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                modifier = Modifier.aspectRatio(1f),
                imageVector = Icons.Default.Image,
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter,
                contentDescription = null
            )
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun Compact(
    state: UserState,
    navActions: UserNavActions
) {

}