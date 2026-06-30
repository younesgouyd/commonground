package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi

interface HomeNavActions {
    fun toEventDetails(id: String)
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
    val events by state.events.collectAsState()
    val searchRequest by state.searchRequest.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        Map(
            modifier = Modifier.weight(.5f).fillMaxHeight(),
            events = events,
            onViewportChanged = { state.onSearchChange(searchRequest.copy(mapViewport = it)) }
        )
        Events(
            modifier = Modifier.weight(.5f).fillMaxHeight(),
            searchRequest = searchRequest,
            events = events,
            navActions = navActions,
            onSearchChange = state.onSearchChange
        )
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
    val events by state.events.collectAsState()
    val searchRequest by state.searchRequest.collectAsState()
    var mapFraction by remember { mutableStateOf(0.4f) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        content = { paddingValues ->
            BoxWithConstraints(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val totalHeightPx = with(LocalDensity.current) { maxHeight.toPx() }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Map(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(this@BoxWithConstraints.maxHeight * mapFraction),
                        events = events,
                        onViewportChanged = { state.onSearchChange(searchRequest.copy(mapViewport = it)) }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .pointerInput(totalHeightPx) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaFraction = dragAmount.y / totalHeightPx
                                    // Bound the layout size between 15% and 85% of total screen real estate
                                    mapFraction = (mapFraction + deltaFraction).coerceIn(0.15f, 0.85f)
                                }
                            }
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.align(Alignment.Center),
                            thickness = 4.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    Events(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        searchRequest = searchRequest,
                        events = events,
                        navActions = navActions,
                        onSearchChange = state.onSearchChange
                    )
                }
            }
        },
        floatingActionButton = {
            SmallFloatingActionButton(
                content = { Icon(Icons.Default.Add, null) },
                onClick = navActions::toCreateEvent
            )
        }
    )
}

