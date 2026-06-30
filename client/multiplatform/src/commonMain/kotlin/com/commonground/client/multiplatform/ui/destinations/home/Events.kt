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
    searchRequest: HomeState.Loaded.SearchRequest,
    events: LazyList<Event>,
    navActions: HomeNavActions,
    onSearchChange: (HomeState.Loaded.SearchRequest) -> Unit
) {
    AdaptiveUi(
        wide = { Wide(modifier, searchRequest, events, navActions, onSearchChange) },
        compact = { Compact(modifier, searchRequest, events, navActions, onSearchChange) }
    )
}

@Composable
fun Wide(
    modifier: Modifier = Modifier,
    searchRequest: HomeState.Loaded.SearchRequest,
    events: LazyList<Event>,
    navActions: HomeNavActions,
    onSearchChange: (HomeState.Loaded.SearchRequest) -> Unit
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
                text = "Events",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            FilledTonalButton(onClick = navActions::toCreateEvent) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Create")
            }
        }
        Search(
            modifier = Modifier.fillMaxWidth(),
            searchRequest = searchRequest,
            onSearchChange = onSearchChange
        )
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
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
    searchRequest: HomeState.Loaded.SearchRequest,
    events: LazyList<Event>,
    navActions: HomeNavActions,
    onSearchChange: (HomeState.Loaded.SearchRequest) -> Unit
) {
    val listState = rememberLazyListState()
    val items by events.items.collectAsState()
    val loading by events.loading.collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LazyColumn(
            modifier = modifier,
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Search(
                    modifier = Modifier.fillMaxWidth(),
                    searchRequest = searchRequest,
                    onSearchChange = onSearchChange
                )
            }
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
    }

    LaunchedEffect(listState, events, items.size) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.map { it == null ||  it >= items.size - 5  }
            .filter { it }
            .collect { events.loadMore() }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Search(
    modifier: Modifier = Modifier,
    searchRequest: HomeState.Loaded.SearchRequest,
    onSearchChange: (HomeState.Loaded.SearchRequest) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            value = searchRequest.title,
            onValueChange = {
                onSearchChange(searchRequest.copy(title = it))
            }
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                val isPrivate = searchRequest.isPrivate == true
                FilterChip(
                    label = { Text("Followers only") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isPrivate) Icons.Default.Done else Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = isPrivate,
                    onClick = {
                        onSearchChange(searchRequest.copy(isPrivate = if (isPrivate) null else true))
                    }
                )
            }
            item {
                val isPrivatePlace = searchRequest.isPrivatePlace == true
                FilterChip(
                    label = { Text("Indoor") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isPrivatePlace) Icons.Default.Done else Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = isPrivatePlace,
                    onClick = {
                        onSearchChange(searchRequest.copy(isPrivatePlace = if (isPrivatePlace) null else true))
                    }
                )
            }
            item {
                val isPaid = searchRequest.isPaid == true
                FilterChip(
                    label = { Text("Paid") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isPaid) Icons.Default.Done else Icons.Default.Paid,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = isPaid,
                    onClick = {
                        onSearchChange(searchRequest.copy(isPaid = if (isPaid) null else true))
                    }
                )
            }
        }
    }
}
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        leadingIcon = { Icon(Icons.Default.Search, null) },
        placeholder = { Text("Search events…") },
        value = value,
        onValueChange = onValueChange,
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

