package com.commonground.client.multiplatform.ui.destinations.eventdetails

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.formatted
import com.commonground.client.multiplatform.ui.toBackendUrl
import com.commonground.client.multiplatform.ui.widgets.Image
import com.commonground.client.multiplatform.ui.widgets.Person
import com.commonground.client.multiplatform.ui.widgets.SystemFilePicker
import com.commonground.core.models.ImageUrl
import kotlinx.coroutines.launch

interface EventDetailsNavActions {
    fun toUser(id: String)
    fun toUpdateEvent()
    fun onBack()
}


@Composable
fun EventDetails(
    viewModel: EventDetailsViewModel,
    navActions: EventDetailsNavActions
) {
    val state by viewModel.state.collectAsState()

    AdaptiveUi(
        wide = { Wide(state, viewModel, navActions) },
        compact = { Compact(state, viewModel, navActions) }
    )
}

@Composable
private fun Wide(
    state: EventDetailsState,
    viewModel: EventDetailsViewModel,
    navActions: EventDetailsNavActions
) {
    when (state) {
        is EventDetailsState.Loading -> LoadingScreen()
        is EventDetailsState.Loaded -> WideLoaded(state, viewModel, navActions)
        is EventDetailsState.NotFound -> EmptyScreen("This event no longer exists")
        is EventDetailsState.Error -> EmptyScreen(state.message)
    }
}

@Composable
private fun Compact(
    state: EventDetailsState,
    viewModel: EventDetailsViewModel,
    navActions: EventDetailsNavActions
) {
    when (state) {
        is EventDetailsState.Loading -> LoadingScreen()
        is EventDetailsState.Loaded -> CompactLoaded(state, viewModel, navActions)
        is EventDetailsState.NotFound -> EmptyScreen("This event no longer exists")
        is EventDetailsState.Error -> EmptyScreen(state.message)
    }
}


@Composable
private fun WideLoaded(
    state: EventDetailsState.Loaded,
    viewModel: EventDetailsViewModel,
    navActions: EventDetailsNavActions
) {
    val event = state.event

    Row(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.width(380.dp).fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EventImage(
                        modifier = Modifier.fillMaxSize(),
                        image = event.image,
                        updateImage = state.updateImage
                    )
                }

                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    MetaRow(Icons.Default.CalendarMonth, "Date", event.startDate.formatted())
                    MetaRow(Icons.Default.Place, "Location", event.locationName)
                    event.duration?.let {
                        MetaRow(
                            icon = Icons.Default.Schedule, label = "Duration",
                            value = it.toComponents { h, m -> if (h > 0) "${h}h ${m}m" else "${m}m" }
                        )
                    }
                    MetaRow(
                        icon = if (event.isPaid) Icons.Default.AttachMoney else Icons.Default.CardGiftcard,
                        label = "Entry",
                        value = if (event.isPaid) "Paid" else "Free"
                    )
                    MetaRow(
                        icon = if (event.isPrivate) Icons.Default.Lock else Icons.Default.Public,
                        label = "Privacy",
                        value = if (event.isPrivate) "Followers only" else "Anyone"
                    )

                    HorizontalDivider()

                    Text(
                        "Hosted by",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    null,
                                    modifier = Modifier.size(22.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Column {
                            Person(
                                name = event.creator.displayName ?: event.creator.username,
                                onClick = { navActions.toUser(event.creator.id) }
                            )
                            Text(
                                "@${event.creator.username}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    if (state.isLoggedInUserEvent) {
                        Button(
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            onClick = navActions::toUpdateEvent,
                            enabled = !state.isBooking,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isBooked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null
                                )
                                Text(
                                    text = "Edit",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    } else {
                        BookingButton(state, viewModel)
                    }
                }
            }
        }

        VerticalDivider()

        Column(
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                val desc = event.description
                if (!desc.isNullOrBlank()) {
                    item {
                        SectionCard(title = "About this event") {
                            Text(
                                desc,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                item { BookingSection(state, viewModel) }

                item { ChatSection(state, viewModel) }
            }
        }
    }
}

@Composable
private fun EventImage(
    modifier: Modifier = Modifier,
    image: ImageUrl?,
    updateImage: suspend (ByteArray) -> ImageUrl?
) {
    val scope = rememberCoroutineScope()
    var image2 by remember(image) { mutableStateOf(image) }
    var showSystemFilePicker by remember { mutableStateOf(false) }

    image2?.let { img ->
        Image(
            modifier = modifier.fillMaxSize(),
            url = img.toBackendUrl(),
            contentScale = ContentScale.FillWidth
        )
    } ?: Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Event Cover",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                )
                TextButton(
                    content = { Text("Set an image") },
                    onClick = { showSystemFilePicker = true }
                )
            }
        }
    }

    if (showSystemFilePicker) {
        SystemFilePicker(
            onFileChosen = {
                showSystemFilePicker = false
                scope.launch {
                    image2 = updateImage(it)
                }
            },
            dismiss = { showSystemFilePicker = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactLoaded(
    state: EventDetailsState.Loaded,
    viewModel: EventDetailsViewModel,
    navActions: EventDetailsNavActions
) {
    val event = state.event

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(event.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = navActions::onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EventImage(
                        modifier = Modifier.fillMaxSize(),
                        image = event.image,
                        updateImage = state.updateImage
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        event.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(event.startDate.formatted(), style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(event.locationName, style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        event.duration?.let {
                            MetaChip(
                                text = it.toComponents { h, m -> if (h > 0) "${h}h ${m}m" else "${m}m" },
                                icon = Icons.Default.Schedule
                            )
                        }
                        MetaChip(
                            if (event.isPaid) "Paid" else "Free",
                            if (event.isPaid) Icons.Default.AttachMoney else Icons.Default.CardGiftcard
                        )
                        if (event.isPrivate) {
                            MetaChip("Followers only", Icons.Default.Lock)
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(Modifier.size(32.dp), CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        Person(
                            name = event.creator.displayName ?: event.creator.username,
                            onClick = { navActions.toUser(event.creator.id) }
                        )
                    }

                    if (state.isLoggedInUserEvent) {
                        Button(
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            onClick = navActions::toUpdateEvent,
                            enabled = !state.isBooking,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isBooked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null
                                )
                                Text(
                                    text = "Edit",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }

                    HorizontalDivider(Modifier.padding(top = 8.dp))
                }
            }

            val desc = event.description
            if (!desc.isNullOrBlank()) {
                item {
                    SectionCard(title = "About this event", modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            desc,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            item { BookingSection(state, viewModel, modifier = Modifier.padding(horizontal = 16.dp)) }

            item { ChatSection(state, viewModel, modifier = Modifier.padding(horizontal = 16.dp)) }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 3.dp)
            Spacer(Modifier.height(16.dp))
            Text("Loading event…", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.EventBusy, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MetaRow(icon: ImageVector, label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
private fun MetaChip(text: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun BookingButton(
    state: EventDetailsState.Loaded,
    viewModel: EventDetailsViewModel
) {
    Button(
        onClick = viewModel::toggleBooking,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        enabled = !state.isBooking,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (state.isBooked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
        )
    ) {
        AnimatedContent(
            targetState = state.isBooking,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { booking ->
            if (booking) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (state.isBooked) Icons.Default.CheckCircle else Icons.Default.BookmarkAdd,
                        null,
                        Modifier.size(20.dp)
                    )
                    Text(
                        if (state.isBooked) "You're going!" else "Book this event",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingSection(
    state: EventDetailsState.Loaded,
    viewModel: EventDetailsViewModel,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Attendees",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        "${state.bookingCount} going",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy((-8).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.tertiaryContainer
                )
                repeat(5) { i ->
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = colors[i].copy(alpha = 0.7f - i * 0.1f),
                        tonalElevation = 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                ('A' + i).toString(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                if (state.bookingCount > 5) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "+${state.bookingCount - 5}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(visible = state.isBooked) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text(
                            "You're booked! See you there 🎉",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            if (!state.isLoggedInUserEvent) {
                BookingButton(state, viewModel)
            }
        }
    }
}

@Composable
private fun ChatSection(
    state: EventDetailsState.Loaded,
    viewModel: EventDetailsViewModel,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Event Chat",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ) {
                    Text(
                        "${state.messages.size} messages",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.heightIn(max = 240.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                state.messages.forEach { msg ->
                    ChatBubble(msg)
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.newMessage,
                    onValueChange = viewModel::onNewMessageChange,
                    placeholder = { Text("Type a message…") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                FilledIconButton(
                    onClick = viewModel::sendMessage,
                    modifier = Modifier.size(48.dp),
                    enabled = state.newMessage.isNotBlank(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Send, "Send", Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {
    val isOwn = msg.isOwn

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
    ) {
        if (!isOwn) {
            Text(
                msg.senderName,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOwn) 16.dp else 4.dp,
                bottomEnd = if (isOwn) 4.dp else 16.dp
            ),
            color = if (isOwn) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = if (isOwn) 0.dp else 1.dp
        ) {
            Text(
                msg.content,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isOwn) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }

        Text(
            msg.timestamp,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(
                start = if (isOwn) 0.dp else 8.dp,
                end = if (isOwn) 8.dp else 0.dp,
                top = 2.dp
            )
        )
    }
}
