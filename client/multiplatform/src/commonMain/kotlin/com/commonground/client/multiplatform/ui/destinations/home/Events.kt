package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.client.multiplatform.ui.formatted
import com.commonground.client.multiplatform.ui.widgets.Badge
import com.commonground.client.multiplatform.ui.widgets.Person
import com.commonground.core.models.Event
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import com.commonground.client.multiplatform.ui.widgets.Image as EventImage

@Composable
fun Events(
    modifier: Modifier,
    events: LazyList<Event>,
    navActions: HomeNavActions
) {
    AdaptiveUi(
        wide = { Wide(modifier, events, navActions) },
        compact = { Compact(modifier, events, navActions) }
    )
}

@Composable
private fun Wide(
    modifier: Modifier,
    events: LazyList<Event>,
    navActions: HomeNavActions
) {
    val listState = rememberLazyGridState()
    val items by events.items.collectAsState()
    val loading by events.loading.collectAsState()

    LazyVerticalGrid(
        modifier = modifier,
        state = listState,
//        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        columns = GridCells.Adaptive(200.dp)
    ) {
        items(items, key = { it.id }) { event ->
            WideEventCard(
                modifier = Modifier.fillMaxWidth().aspectRatio(.55f),
                event = event,
                onClick = { navActions.toEventDetails(event.id) }
            )
        }
        if (!loading && items.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyEvents(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp))
            }
        }
        if (loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LoadingMore(modifier = Modifier.fillMaxWidth().padding(16.dp))
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
private fun Compact(
    modifier: Modifier,
    events: LazyList<Event>,
    navActions: HomeNavActions
) {
    val listState = rememberLazyListState()
    val items by events.items.collectAsState()
    val loading by events.loading.collectAsState()

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { event ->
            CompactEventCard(
                event = event,
                onClick = { navActions.toEventDetails(event.id) },
                onUserClick = { navActions.toUser(event.creator.id) }
            )
        }
        if (!loading && items.isEmpty()) {
            item {
                EmptyEvents(modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp))
            }
        }
        if (loading) {
            item {
                LoadingMore(modifier = Modifier.fillMaxWidth().padding(16.dp))
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
private fun WideEventCard(
    modifier: Modifier,
    event: Event,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EventArtwork(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1.45f),
                    image = event.image
                )
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        // Calculates and enforces the height range of exactly 2 lines based on typography line-height
                        modifier = Modifier.heightIn(
                            min = MaterialTheme.typography.titleMedium.lineHeight.value.dp * 2
                        ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = event.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        minLines = 3,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Badges(event)
                Person(event.creator.displayName ?: event.creator.username)
                Location(event.locationName)
                Date(event.startDate.formatted())
            }
        }
    }
}

@Composable
private fun CompactEventCard(
    event: Event,
    onClick: () -> Unit,
    onUserClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 112.dp).padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EventArtwork(
                modifier = Modifier.size(92.dp),
                image = event.image
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Location(event.locationName)
                Badges(event)
                Person(
                    name = event.creator.displayName ?: event.creator.username,
                    onClick = onUserClick
                )
            }
        }
    }
}

@Composable
private fun EventArtwork(
    modifier: Modifier,
    image: String?
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (image == null) {
                Icon(
                    modifier = Modifier.size(42.dp),
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else {
                EventImage(
                    modifier = Modifier.fillMaxSize(),
                    url = image,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


@Composable
private fun Person(name: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Badges(event: Event) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (event.isPrivate) {
                item { Badge("Indoor", Icons.Default.Home) }
            }
            if (event.isPaid) {
                item { Badge("Paid", Icons.Default.Paid) }
            }
        }
    }
}

@Composable
private fun Date(datetime: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Default.Alarm,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = datetime,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Location(locationName: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = locationName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyEvents(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "No events in this area",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Move the map or zoom out to discover more.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingMore(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 2.dp)
    }
}
