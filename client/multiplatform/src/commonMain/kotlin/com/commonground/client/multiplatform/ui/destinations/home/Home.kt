package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Row(modifier = Modifier.fillMaxSize()) {
        Map(
            modifier = Modifier.weight(.5f).fillMaxHeight(),
            events = state.events,
            currentLocation = state.currentLocation,
            onViewportChanged = state.onMapViewportChanged
        )
        Events(
            modifier = Modifier.weight(.5f).fillMaxHeight(),
            events = state.events,
            navActions = navActions
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Map(
            modifier = Modifier.fillMaxWidth().weight(.5f),
            events = state.events,
            currentLocation = state.currentLocation,
            onViewportChanged = state.onMapViewportChanged
        )
        Events(
            modifier = Modifier.fillMaxWidth().weight(.5f),
            events = state.events,
            navActions = navActions
        )
    }
}
