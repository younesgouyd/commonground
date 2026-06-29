package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.LazyList
import com.commonground.client.multiplatform.ui.widgets.CompactEventCard
import com.commonground.client.multiplatform.ui.widgets.ProgressIndicator
import com.commonground.client.multiplatform.ui.widgets.WideEventCard
import com.commonground.core.models.Event
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

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
fun Wide(
    modifier: Modifier = Modifier,
    events: LazyList<Event>,
    navActions: HomeNavActions
) {
    val items by events.items.collectAsState()
    val loadingItems by events.loading.collectAsState()
    val scrollState = rememberLazyGridState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Events",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            FilledTonalButton(onClick = navActions::toCreateEvent) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Create")
            }
        }
        SearchBar(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        Surface(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                state = scrollState,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                columns = GridCells.Adaptive(200.dp)
            ) {
                items(items) { event ->
                    WideEventCard(
                        modifier = Modifier.aspectRatio(.75f),
                        event = event,
                        onClick = { navActions.toEventDetails(event.id) }
                    )
                }
                if (loadingItems) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ProgressIndicator()
                    }
                }
            }
        }
    }

    LaunchedEffect(scrollState, items) {
        snapshotFlow {
            scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.map { it == null || it >= items.size - 5 }
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(items, key = { it.id }) { event ->
            CompactEventCard(
                event = event,
                onClick = { navActions.toEventDetails(event.id) }
            )
        }
        if (loading) {
            item { ProgressIndicator() }
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
private fun SearchBar(modifier: Modifier = Modifier) {
    var value by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = modifier,
        leadingIcon = { Icon(Icons.Default.Search, null) },
        placeholder = { Text("Search events…") },
        value = value,
        onValueChange = { value = it },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
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

