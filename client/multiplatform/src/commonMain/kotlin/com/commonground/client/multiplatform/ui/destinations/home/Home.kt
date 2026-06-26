package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.widgets.EventCard
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

interface HomeNavActions {
    fun toEventDetails(id: String)
    fun toUser(id: String)
    fun toCreateEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    viewModel: HomeViewModel,
    navActions: HomeNavActions
) {
    val state by viewModel.state.collectAsState()

    AdaptiveUi(
        wide = { Wide(state, navActions) },
        compact = { Compact(state, navActions) }
    )
}

@Composable
private fun Wide(
    state: HomeState,
    navActions: HomeNavActions
) {
    when (state) {
        is HomeState.Loading -> Text("Loading...")
        is HomeState.Loaded -> Wide(state, navActions)
        is HomeState.Error -> Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun Wide(
    state: HomeState.Loaded,
    navActions: HomeNavActions
) {
    val items by state.events.items.collectAsState()
    val loadingItems by state.events.loading.collectAsState()
    val scrollState = rememberLazyGridState()

    Row(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.weight(0.55f).fillMaxHeight(),
            tonalElevation = 1.dp
        ) {
            Map(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                items = items,
                currentLocation = state.currentLocation,
                onViewportChanged = state.onMapViewportChanged
            )
        }

        Column(
            modifier = Modifier.weight(0.45f).fillMaxHeight()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
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
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    columns = GridCells.Adaptive(200.dp)
                ) {
                    items(items) { event ->
                        EventCard(
                            event = event,
                            onClick = { navActions.toEventDetails(event.id) },
                            onUserClick = { navActions.toUser(it) }
                        )
                    }
                    if (loadingItems) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
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
            .collect { state.events.loadMore() }
    }
}


@Composable
private fun Compact(
    state: HomeState,
    navActions: HomeNavActions
) {
    when (state) {
        is HomeState.Loading -> Text("Loading...")
        is HomeState.Loaded -> Compact(state, navActions)
        is HomeState.Error -> Text(
            text = "Something went wrong",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Compact(
    state: HomeState.Loaded,
    navActions: HomeNavActions
) {
    val items by state.events.items.collectAsState()
    val loadingItems by state.events.loading.collectAsState()
    val scrollState = rememberLazyGridState()

    val sheetState = rememberBottomSheetScaffoldState()
    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = 200.dp,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            sheetTonalElevation = 4.dp,
            sheetContent = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .padding(top = 10.dp, bottom = 4.dp),
                            shape = RoundedCornerShape(2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        ) {}
                    }

                    SearchBar(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        state = scrollState,
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        columns = GridCells.Fixed(2)
                    ) {
                        items(items) { event ->
                            EventCard(
                                event = event,
                                onClick = { navActions.toEventDetails(event.id!!) },
                                onUserClick = { navActions.toUser(it) }
                            )
                        }
                        if (loadingItems) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Map(
                modifier = Modifier.fillMaxSize().padding(padding),
                items = items,
                currentLocation = state.currentLocation,
                onViewportChanged = state.onMapViewportChanged
            )
        }

        FloatingActionButton(
            onClick = navActions::toCreateEvent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Event")
        }

    }
    LaunchedEffect(scrollState, items) {
        snapshotFlow {
            scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.map { it == null || it >= items.size - 5 }
            .filter { it }
            .collect { state.events.loadMore() }
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
