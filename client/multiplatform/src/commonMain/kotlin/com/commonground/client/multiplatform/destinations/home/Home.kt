package com.commonground.client.multiplatform.destinations.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.Event

interface HomeNavActions {
    fun toEventDetails(id: Long)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    viewModel: HomeViewModel,
    navActions: HomeNavActions
) {
    Events(
        events = viewModel.events,
        onEventClick = navActions::toEventDetails
    )
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
private fun Events(
    events: List<Event>,
    onEventClick: (id: Long) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            columns = GridCells.Adaptive(200.dp)
        ) {
            items(events) { event ->
                Event(
                    event = event,
                    onClick = { onEventClick(event.id) }
                )
            }
        }
    }
}

@Composable
private fun Event(
    event: Event,
    onClick: () -> Unit
) {
    Card(onClick) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                modifier = Modifier.aspectRatio(1f),
                imageVector = Icons.Default.Image,
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter,
                contentDescription = null
            )
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
