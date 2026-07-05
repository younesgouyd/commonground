package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
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
        is HomeState.Loading -> WideLoading()
        is HomeState.Loaded -> Wide(state, navActions)
        is HomeState.Error -> WideError()
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
        // Map — dominant side
        Box(modifier = Modifier.weight(.55f).fillMaxHeight()) {
            Map(
                modifier = Modifier.fillMaxSize(),
                events = events,
                onViewportChanged = { state.onSearchChange(searchRequest.copy(mapViewport = it)) },
                navActions = navActions
            )
            // Subtle gradient at bottom edge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
        }

        // Events panel
        Surface(
            modifier = Modifier.weight(.45f).fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Events(
                modifier = Modifier.fillMaxSize(),
                searchRequest = searchRequest,
                events = events,
                navActions = navActions,
                onSearchChange = state.onSearchChange
            )
        }
    }
}

@Composable
private fun WideLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun WideError() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Something went wrong", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun Compact(
    state: HomeState,
    navActions: HomeNavActions
) {
    when (state) {
        is HomeState.Loading -> WideLoading()
        is HomeState.Loaded -> Compact(state, navActions)
        is HomeState.Error -> WideError()
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
        containerColor = MaterialTheme.colorScheme.background,
        content = { paddingValues ->
            BoxWithConstraints(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val totalHeightPx = with(LocalDensity.current) { maxHeight.toPx() }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Map
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(this@BoxWithConstraints.maxHeight * mapFraction),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                        tonalElevation = 3.dp
                    ) {
                        Map(
                            modifier = Modifier.fillMaxSize(),
                            events = events,
                            onViewportChanged = { state.onSearchChange(searchRequest.copy(mapViewport = it)) },
                            navActions = navActions
                        )
                    }

                    // Drag handle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .pointerInput(totalHeightPx) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaFraction = dragAmount.y / totalHeightPx
                                    mapFraction = (mapFraction + deltaFraction).coerceIn(0.15f, 0.85f)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colorScheme.outlineVariant
                        ) {}
                    }

                    // Events list
                    Events(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        searchRequest = searchRequest,
                        events = events,
                        navActions = navActions,
                        onSearchChange = state.onSearchChange
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navActions::toCreateEvent,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create event")
            }
        }
    )
}

