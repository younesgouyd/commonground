package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.core.models.Event
import com.commonground.core.models.User
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class Events(
    val created: LazyList<Event>,
    val attending: LazyList<Event>,
    val went: LazyList<Event>
)

data class Follows(
    val followers: StateFlow<LazyList<User>>,
    val following: StateFlow<LazyList<User>>,
    val onFollowUserClick: suspend (userId: String) -> Unit,
    val onUnfollowUserClick: suspend (userId: String) -> Unit
)

interface ProfileNavActions {
    fun toEvent(id: String)
    fun toUser(id: String)
    fun toCreateEvent()
}

private enum class ProfileTabs { Events, Follows }

private enum class FollowsTabs { Followers, Following }

private data class ExpandedSection(
    val title: String,
    val events: LazyList<Event>,
)

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    events: Events,
    follows: Follows,
    navActions: ProfileNavActions
) {
    val tabs = remember { ProfileTabs.entries }
    var selectedTab by remember { mutableStateOf(ProfileTabs.Events) }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        text = { Text(tab.name) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }

            when (selectedTab) {
                ProfileTabs.Events -> EventsTab(events, navActions::toEvent)
                ProfileTabs.Follows -> FollowsTab(follows, navActions)
            }
        }
    }
}

@Composable
fun StatItem(count: Long, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EventsTab(
    events: Events,
    onEventClick: (id: String) -> Unit
) {
    val createdItems by events.created.items.collectAsState()
    val goingItems by events.attending.items.collectAsState()
    val wentItems by events.went.items.collectAsState()

    if (createdItems.isEmpty() && goingItems.isEmpty() && wentItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.EventBusy,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text("No events yet", style = MaterialTheme.typography.bodyLarge)
            }
        }
        return
    }

    var expandedSection by remember { mutableStateOf<ExpandedSection?>(null) }

    if (expandedSection != null) {
        ShowAllEventsGrid(
            title = expandedSection!!.title,
            events = expandedSection!!.events,
            onBack = { expandedSection = null },
            onEventClick = onEventClick
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            EventSection(
                title = "Created",
                events = events.created,
                onShowAll = { expandedSection = ExpandedSection("Created", events.created) },
                onEventClick = onEventClick
            )
            EventSection(
                title = "Going",
                events = events.attending,
                onShowAll = { expandedSection = ExpandedSection("Going", events.attending) },
                onEventClick = onEventClick
            )
            EventSection(
                title = "Went",
                events = events.went,
                onShowAll = { expandedSection = ExpandedSection("Went", events.went) },
                onEventClick = onEventClick
            )
        }
    }
}

@Composable
private fun ShowAllEventsGrid(
    title: String,
    events: LazyList<Event>,
    onBack: () -> Unit,
    onEventClick: (String) -> Unit
) {
    val listState = rememberLazyGridState()
    val items by events.items.collectAsState()
    val loading by events.loading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }

        HorizontalDivider()

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            columns = GridCells.Adaptive(200.dp)
        ) {
            items(items, key = { it.id }) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event.id) },
                    onUserClick = {}
                )
            }
            if (loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 2.dp)
                    }
                }
            }
        }
    }

    LaunchedEffect(listState, events, items.size) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.map { it == null ||  it >= items.size - 5  }
            .filter { it }
            .collect { events.loadMore() }
    }
}

@Composable
private fun EventSection(
    title: String,
    events: LazyList<Event>,
    onShowAll: () -> Unit,
    onEventClick: (id: String) -> Unit
) {
    val listState = rememberLazyListState()
    val items by events.items.collectAsState()
    val loading by events.loading.collectAsState()
    val count by events.totalCount.collectAsState()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            TextButton(onClick = onShowAll) {
                Text("Show all")
            }
        }

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event.id) },
                    onUserClick = {} // TODO
                )
            }
            if (loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 2.dp)
                    }
                }
            }
        }
    }

    LaunchedEffect(listState, events, items.size) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.map { it == null ||  it >= items.size - 5  }
            .filter { it }
            .collect { events.loadMore() }
    }
}

@Composable
private fun FollowsTab(
    follows: Follows,
    navActions: ProfileNavActions
) {
    val tabs = remember { FollowsTabs.entries }
    var selectedTab by remember { mutableStateOf(FollowsTabs.Followers) }
    val followers by follows.followers.collectAsState()
    val following by follows.following.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedTab.ordinal
        ) {
            for (tab in tabs) {
                Tab(
                    text = { Text(tab.name) },
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab }
                )
            }
        }

        when (selectedTab) {
            FollowsTabs.Followers -> FollowsSubTab("followers", followers, follows.onFollowUserClick, follows.onUnfollowUserClick, navActions)
            FollowsTabs.Following -> FollowsSubTab("following", following, follows.onFollowUserClick, follows.onUnfollowUserClick, navActions)
        }
    }
}

@Composable
private fun FollowsSubTab(
    followsTypeLabel: String,
    users: LazyList<User>,
    onFollowUserClick: suspend (userId: String) -> Unit,
    onUnfollowUserClick: suspend (userId: String) -> Unit,
    navActions: ProfileNavActions
) {
    val listState = rememberLazyListState()
    val items by users.items.collectAsState()
    val loading by users.loading.collectAsState()

    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text("No $followsTypeLabel yet", style = MaterialTheme.typography.bodyLarge)
            }
        }
        return
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Adaptive(300.dp)
    ) {
        items(items) { user ->
            UserCard(
                user = user,
                onClick = { navActions.toUser(user.id) },
                onFollowClick = { onFollowUserClick(user.id) },
                onUnfollowClick = { onUnfollowUserClick(user.id) }
            )
        }
        if (loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 2.dp)
                }
            }
        }
    }

    LaunchedEffect(listState, users, items.size) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.map { it == null ||  it >= items.size - 5  }
            .filter { it }
            .collect { users.loadMore() }
    }
}

@Composable
private fun UserCard(
    user: User,
    onClick: () -> Unit,
    onFollowClick: suspend () -> Unit,
    onUnfollowClick: suspend () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }

    Card(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Column {
                Text(
                    text = user.displayName ?: user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (user.displayName != null) {
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (user.isFollowed != null) {
                Spacer(modifier = Modifier.weight(1f))
                if (user.isFollowed == true) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                isSubmitting = true
                                try {
                                    onUnfollowClick()
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                text = "Following",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                isSubmitting = true
                                try {
                                    onFollowClick()
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "Follow",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}