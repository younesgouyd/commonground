package com.commonground.client.multiplatform.ui.destinations.eventdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.widgets.ItemDetailsHeaderWide
import com.commonground.client.multiplatform.ui.widgets.Person
import com.commonground.core.UserId

interface EventDetailsNavActions {
    fun toUser(id: UserId)
}

@Composable
fun EventDetails(
    viewModel: EventDetailsViewModel,
    navActions: EventDetailsNavActions
) {
    val state by viewModel.state.collectAsState()

    AdaptiveUi(
        wide = { Wide(state, navActions) },
        compact = { Compact(state, navActions) }
    )

}

@Composable
private fun Wide(
    state: EventDetailsState,
    navActions: EventDetailsNavActions
) {
    when (state) {
        is EventDetailsState.Loading -> Text("Loading...")
        is EventDetailsState.Loaded -> { Wide(state, navActions) }
        is EventDetailsState.NotFound -> Text("This event no longer exists")
    }
}

@Composable
private fun Wide(
    state: EventDetailsState.Loaded,
    navActions: EventDetailsNavActions
) {
    val event = state.event

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn {
            item {
                ItemDetailsHeaderWide(
                    modifier = Modifier.fillMaxWidth().height(500.dp),
                    title = event.title,
                    image = null,
                    itemAttributes = {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(state.creators) { user ->
                                Person(
                                    name = user.displayName ?: user.username,
                                    onClick = { navActions.toUser(user.id) }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Compact(
    state: EventDetailsState,
    navActions: EventDetailsNavActions
) {
    when (state) {
        is EventDetailsState.Loading -> Text("Loading...")
        is EventDetailsState.Loaded -> { Wide(state, navActions) }
        is EventDetailsState.NotFound -> Text("This event no longer exists")
    }
}

@Composable
private fun Compact(
    state: EventDetailsState.Loaded,
    navActions: EventDetailsNavActions
) {

}