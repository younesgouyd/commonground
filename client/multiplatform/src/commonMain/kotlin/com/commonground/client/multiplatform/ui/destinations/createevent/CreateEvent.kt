package com.commonground.client.multiplatform.ui.destinations.createevent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.Platform
import com.commonground.client.multiplatform.platform
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.widgets.*
import com.commonground.core.models.Coordinates
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

interface CreateEventNavActions {
    fun onBack()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent(
    viewModel: CreateEventViewModel,
    navActions: CreateEventNavActions
) {
    val state by viewModel.state.collectAsState()

    AdaptiveUi(
        wide = { Wide(state, navActions) },
        compact = { Compact(state, navActions) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Wide(
    state: CreateEventState,
    navActions: CreateEventNavActions
) {
    Row(modifier = Modifier.fillMaxSize()) {
        ScreenHeroHeader()
        VerticalDivider()
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            CenterAlignedTopAppBar(
                title = { Text("Create Event") },
                navigationIcon = {
                    IconButton(onClick = navActions::onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 40.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CreateEventForm(
                    isSubmitting = state.isSubmitting,
                    onSubmitClick = state.onSubmit
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Compact(
    state: CreateEventState,
    navActions: CreateEventNavActions
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Event") },
                navigationIcon = {
                    IconButton(onClick = navActions::onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CreateEventForm(
                isSubmitting = state.isSubmitting,
                onSubmitClick = state.onSubmit
            )
        }
    }
}

@Composable
private fun ScreenHeroHeader() {
    Surface(
        modifier = Modifier.width(320.dp).fillMaxHeight(),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AddCircle,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Create Event",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Fill in the details to share your event with the community.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun CreateEventForm(
    isSubmitting: StateFlow<Boolean>,
    onSubmitClick: (
        title: String,
        description: String,
        locationName: String,
        coordinates: Coordinates,
        startDate: Instant,
        endDate: Instant?,
        isPrivate: Boolean,
        isPrivatePlace: Boolean,
        isPaid: Boolean,
        image: ByteArray?
    ) -> Unit
) {
    val isSubmitting by isSubmitting.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var coordinates by remember { mutableStateOf<Coordinates?>(null) }
    var startDate by remember { mutableStateOf<Instant?>(Clock.System.now() + 1.hours) }
    var endDate by remember { mutableStateOf<Instant?>(null) }
    var isPrivate by remember { mutableStateOf(false) }
    var isPrivatePlace by remember { mutableStateOf(false) }
    var isPaid by remember { mutableStateOf(false) }
    var image by remember { mutableStateOf<ByteArray?>(null) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var locationNameError by remember { mutableStateOf<String?>(null) }
    var coordinatesError by remember { mutableStateOf<String?>(null) }

    val canSubmit = !isSubmitting
            && title.isNotBlank()
            && locationName.isNotBlank()
            && coordinates != null
            && startDate != null

    ImageField(
        modifier = Modifier.fillMaxWidth(),
        value = image,
        onValueChange = { image = it }
    )
    TitleField(
        modifier = Modifier.fillMaxWidth(),
        value = title,
        error = titleError,
        onValueChange = {
            title = it
            titleError = if (title.isBlank()) "Title is required" else null
        }
    )
    DescriptionField(
        modifier = Modifier.fillMaxWidth(),
        value = description,
        onValueChange = { description = it }
    )
    LocationNameField(
        modifier = Modifier.fillMaxWidth(),
        value = locationName,
        error = locationNameError,
        onValueChange = {
            locationName = it
            locationNameError = if (locationName.isBlank()) "Location is required" else null
        }
    )
    DateTimeField(
        modifier = Modifier.fillMaxWidth(),
        label = "Start date",
        value = startDate,
        error = if (startDate == null) "You must specify a start date." else null,
        onValueChange = { startDate = it }
    )
    DateTimeField(
        modifier = Modifier.fillMaxWidth(),
        label = "End date",
        value = endDate,
        error = null,
        onValueChange = { endDate = it }
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            SwitchRow(
                icon = Icons.Default.Lock,
                label = "Privacy",
                description = "Only followers can see this event",
                checked = isPrivate,
                onCheckedChange = { isPrivate = it }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SwitchRow(
                icon = Icons.Default.Home,
                label = "Private place",
                description = "The event takes place inside a private or restricted venue like a home, hotel, restaurant, or school",
                checked = isPrivatePlace,
                onCheckedChange = { isPrivatePlace = it }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SwitchRow(
                icon = Icons.Default.AttachMoney,
                label = "Paid event",
                description = "Attendees need to purchase a ticket",
                checked = isPaid,
                onCheckedChange = { isPaid = it }
            )
        }
    }
    AdaptiveUi(
        wide = {
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(2.seconds)
                visible = true
            }
            if (platform == Platform.ANDROID || visible) {
                CoordinatesForm(
                    modifier = Modifier.fillMaxWidth(),
                    value = coordinates,
                    error = coordinatesError,
                    onValueChange = {
                        coordinates = it
                        coordinatesError = if (it == null) "You must set valid coordinates." else null
                    }
                )
            }
        },
        compact = {
            CoordinatesSection(
                modifier = Modifier.fillMaxWidth(),
                value = coordinates,
                onValueChange = { coordinates = it }
            )
        }
    )
    Spacer(Modifier.height(8.dp))
    Button(
        modifier = Modifier.fillMaxWidth().height(52.dp),
        onClick = {
            onSubmitClick(
                title,
                description,
                locationName,
                coordinates!!,
                startDate!!,
                endDate,
                isPrivate,
                isPrivatePlace,
                isPaid,
                image
            )
        },
        enabled = canSubmit,
        shape = RoundedCornerShape(14.dp)
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Create Event", style = MaterialTheme.typography.titleMedium)
        }
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun ImageField(
    modifier: Modifier,
    value: ByteArray?,
    onValueChange: (ByteArray?) -> Unit
) {
    var showSystemFilePicker by remember { mutableStateOf(false) }

    if (value == null) {
        TextButton(
            modifier = modifier,
            onClick = { showSystemFilePicker = true }
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Set an image")
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                data = value
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showSystemFilePicker = true }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, null)
                        Text("Change")
                    }
                }
                Button(
                    onClick = { onValueChange(null) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Clear, null)
                        Text("Clear")
                    }
                }
            }
        }
    }

    if (showSystemFilePicker) {
        SystemFilePicker(
            onFileChosen = {
                showSystemFilePicker = false
                onValueChange(it)
            },
            dismiss = { showSystemFilePicker = false }
        )
    }
}

@Composable
private fun TitleField(
    modifier: Modifier = Modifier,
    value: String,
    error: String?,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text("Event title") },
        placeholder = { Text("Give your event a name") },
        leadingIcon = { Icon(Icons.Default.Edit, null) },
        isError = error != null,
        supportingText = if (error != null) { @Composable { Text(error) } } else null,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun DescriptionField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text("Description") },
        placeholder = { Text("What's this event about?") },
        minLines = 3,
        maxLines = 5,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun LocationNameField(
    modifier: Modifier = Modifier,
    value: String,
    error: String?,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text("Location") },
        placeholder = { Text("Where will this event take place?") },
        leadingIcon = { Icon(Icons.Default.Place, null) },
        isError = error != null,
        supportingText = if (error != null) { @Composable { Text(error) } } else null,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun CoordinatesSection(
    modifier: Modifier = Modifier,
    value: Coordinates?,
    onValueChange: (Coordinates?) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = { showForm = true }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Coordinates",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = value?.latitude?.toString() ?: "",
                    onValueChange = {},
                    label = { Text("Latitude") },
                    placeholder = { Text("e.g. 40.7128") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    readOnly = true
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = value?.longitude?.toString() ?: "",
                    onValueChange = {},
                    label = { Text("Longitude") },
                    placeholder = { Text("e.g. -74.0060") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    minLines = 1,
                    maxLines = 1,
                    readOnly = true
                )
            }
        }
    }

    if (showForm) {
        CoordinatesFormDialog(
            value = value,
            error = error,
            onValueChange = {
                onValueChange(it)
                error = if (it == null) "You must set valid coordinates." else null
            },
            onDismissRequest = { showForm = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurationDropdown(
    modifier: Modifier = Modifier,
    selected: Long,
    onChange: (Long) -> Unit
) {
    val options = listOf(
        DropdownOption("15 minutes", 15L),
        DropdownOption("30 minutes", 30L),
        DropdownOption("45 minutes", 45L),
        DropdownOption("1 hour", 60L),
        DropdownOption("1.5 hours", 90L),
        DropdownOption("2 hours", 120L),
        DropdownOption("3 hours", 180L),
        DropdownOption("4 hours", 240L)
    )
    val selectedLabel = options.find { it.value == selected }?.label ?: options[3].label

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Duration") },
            leadingIcon = { Icon(Icons.Default.Timer, null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onChange(option.value as Long)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}