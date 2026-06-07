package com.commonground.client.multiplatform.ui.destinations.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.core.models.Event

interface ProfileNavActions {
    fun toFollowers(id: String)
    fun toFollowing(id: String)
    fun toEvent(id: String)
    fun toUser(id: String)
}

private enum class ProfileTabs { Events, Friends }

@Composable
fun Profile(
    viewModel: ProfileViewModel,
    navActions: ProfileNavActions
) {
    val state by viewModel.state.collectAsState()

    AdaptiveUi(
        wide = { Wide(state, navActions) },
        compact = { Compact(state, navActions) }
    )
}

@Composable
private fun Wide(
    state: ProfileState,
    navActions: ProfileNavActions
) {
    when (state) {
        is ProfileState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProfileState.Loaded -> Wide(state, navActions)
        is ProfileState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun Wide(
    state: ProfileState.Loaded,
    navActions: ProfileNavActions
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Surface(
            modifier = Modifier.width(320.dp).fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 2.dp
        ) {
            ProfileSidebar(state)
        }

        Surface(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileContent(state, navActions)
        }
    }
}

@Composable
private fun ProfileSidebar(
    state: ProfileState.Loaded
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(80.dp),
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile picture",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = state.user.displayName ?: state.user.username,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "@${state.user.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val bio = state.user.bio
        if (!bio.isNullOrBlank()) {
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(count = state.eventCount, label = "Events")
            StatItem(count = state.friendCount, label = "Friends")
        }

        HorizontalDivider()

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = state.onEditProfile
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Edit Profile")
            }
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = state.onToSettings
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Settings")
            }
        }
    }
}

@Composable
private fun StatItem(count: Int, label: String) {
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
private fun ProfileContent(
    state: ProfileState.Loaded,
    navActions: ProfileNavActions
) {
    val tabs = remember { ProfileTabs.entries }
    var selectedTab by remember { mutableStateOf(ProfileTabs.Events) }

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
            ProfileTabs.Events -> EventsTab(state, navActions)
            ProfileTabs.Friends -> FriendsTab(state, navActions)
        }
    }
}

private data class ExpandedSection(
    val title: String,
    val events: List<Event>,
)

@Composable
private fun EventsTab(
    state: ProfileState.Loaded,
    navActions: ProfileNavActions
) {
    if (state.events.created.isEmpty() && state.events.going.isEmpty() && state.events.went.isEmpty()) {
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
            onEventClick = { eventId -> navActions.toEvent(eventId) }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (state.events.created.isNotEmpty()) {
                EventSection(
                    title = "Created",
                    count = state.events.created.size,
                    events = state.events.created,
                    onShowAll = { expandedSection = ExpandedSection("Created", state.events.created) },
                    onEventClick = { navActions.toEvent(it.id) }
                )
            }

            if (state.events.going.isNotEmpty()) {
                EventSection(
                    title = "Going",
                    count = state.events.going.size,
                    events = state.events.going,
                    onShowAll = { expandedSection = ExpandedSection("Going", state.events.going) },
                    onEventClick = { navActions.toEvent(it.id) }
                )
            }

            if (state.events.went.isNotEmpty()) {
                EventSection(
                    title = "Went",
                    count = state.events.went.size,
                    events = state.events.went,
                    onShowAll = { expandedSection = ExpandedSection("Went", state.events.went) },
                    onEventClick = { navActions.toEvent(it.id) }
                )
            }
        }
    }
}

@Composable
private fun ShowAllEventsGrid(
    title: String,
    events: List<Event>,
    onBack: () -> Unit,
    onEventClick: (String) -> Unit
) {
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
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            columns = GridCells.Adaptive(200.dp)
        ) {
            items(events, key = { it.id }) { event ->
                HorizontalEventCard(
                    event = event,
                    onClick = { onEventClick(event.id) }
                )
            }
        }
    }
}

@Composable
private fun EventSection(
    title: String,
    count: Int,
    events: List<Event>,
    onShowAll: () -> Unit,
    onEventClick: (Event) -> Unit
) {
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events, key = { it.id }) { event ->
                HorizontalEventCard(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
private fun HorizontalEventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(180.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = event.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FriendsTab(
    state: ProfileState.Loaded,
    navActions: ProfileNavActions
) {
    if (state.friends.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text("No friends yet", style = MaterialTheme.typography.bodyLarge)
            }
        }
        return
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        columns = GridCells.Adaptive(160.dp)
    ) {
        items(state.friends) { friend ->
            FriendCard(
                friend = friend,
                onClick = { navActions.toUser(friend.id) }
            )
        }
    }
}

@Composable
private fun FriendCard(
    friend: ProfileState.Loaded.Friend,
    onClick: () -> Unit
) {
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
                    text = friend.displayName ?: friend.username,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (friend.displayName != null) {
                    Text(
                        text = "@${friend.username}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun Compact(
    state: ProfileState,
    navActions: ProfileNavActions
) {
    when (state) {
        is ProfileState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProfileState.Loaded -> Compact(state, navActions)
        is ProfileState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun Compact(
    state: ProfileState.Loaded,
    navActions: ProfileNavActions
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            modifier = Modifier.size(56.dp),
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile picture",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = state.user.displayName ?: state.user.username,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "@${state.user.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val bio = state.user.bio
                if (!bio.isNullOrBlank()) {
                    Text(
                        text = bio,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(count = state.eventCount, label = "Events")
                    StatItem(count = state.friendCount, label = "Friends")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = state.onEditProfile
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Edit", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = state.onToSettings
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Settings", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        ProfileContent(state, navActions)
    }
}
