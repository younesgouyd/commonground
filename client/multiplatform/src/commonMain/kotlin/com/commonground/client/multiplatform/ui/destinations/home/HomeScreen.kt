package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi

interface HomeNavActions {
    fun toEventDetails(id: String)
    fun toUser(id: String)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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

    Surface(color = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Events(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp),
                events = state.events,
                navActions = navActions
            )
            Map(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                items = items,
                currentLocation = state.currentLocation,
                onViewportChanged = state.onMapViewportChanged
            )
        }
    }
}


// TODO: check androidx.compose.material3.SearchBar
@Composable
private fun Search(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    var value by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = modifier,
        leadingIcon = { Icon(Icons.Default.Search, null) },
        label = { Text("Search") },
        value = value,
        onValueChange = { value = it }
    )
}

@Composable
private fun Compact(
    state: HomeState,
    navActions: HomeNavActions
) {
    when (state) {
        is HomeState.Loading -> Text("Loading...")
        is HomeState.Loaded -> Compact(state, navActions)
        is HomeState.Error -> Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun Compact(
    state: HomeState.Loaded,
    navActions: HomeNavActions
) {
    val items by state.events.items.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Map(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                items = items,
                currentLocation = state.currentLocation,
                onViewportChanged = state.onMapViewportChanged
            )
            Events(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp),
                events = state.events,
                navActions = navActions
            )
        }
    }
}
