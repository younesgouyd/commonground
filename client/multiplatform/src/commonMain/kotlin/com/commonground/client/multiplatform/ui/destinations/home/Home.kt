package com.commonground.client.multiplatform.ui.destinations.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.widgets.Duration
import com.commonground.client.multiplatform.ui.widgets.Person
import com.commonground.core.Event
import com.commonground.core.EventId
import com.commonground.core.UserId

interface HomeNavActions {
    fun toEventDetails(id: EventId)
    fun toUser(id: UserId)
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
    Surface(color = MaterialTheme.colorScheme.background) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            columns = GridCells.Adaptive(200.dp)
        ) {
            items(state.events) { event ->
                Event(
                    event = event,
                    onClick = { navActions.toEventDetails(event.id) },
                    onUserClick = { navActions.toUser(it) }
                )
            }
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
private fun Event(
    event: Event,
    onClick: () -> Unit,
    onUserClick: (UserId) -> Unit
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
            Duration(event.duration)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By:",
                    style = MaterialTheme.typography.labelMedium
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(event.creators) { creator ->
                        Person(
                            name = creator.displayName ?: creator.username,
                            onClick = { onUserClick(creator.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Compact(
    state: HomeState,
    navActions: HomeNavActions
) {

}
