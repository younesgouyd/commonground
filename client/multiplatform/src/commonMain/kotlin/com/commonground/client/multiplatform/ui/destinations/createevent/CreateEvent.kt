package com.commonground.client.multiplatform.ui.destinations.createevent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.AdaptiveUi
import com.commonground.client.multiplatform.ui.widgets.DropdownOption

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
        wide = { Wide(state, viewModel, navActions) },
        compact = { Compact(state, viewModel, navActions) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Wide(
    state: CreateEventState,
    viewModel: CreateEventViewModel,
    navActions: CreateEventNavActions
) {
    Row(modifier = Modifier.fillMaxSize()) {
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
                    text = "Create an Event",
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

        VerticalDivider()

        Column(
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            CenterAlignedTopAppBar(
                title = { Text("New Event") },
                navigationIcon = {
                    IconButton(onClick = navActions::onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
                CreateEventForm(state, viewModel, isWide = true)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Compact(
    state: CreateEventState,
    viewModel: CreateEventViewModel,
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
            CreateEventForm(state, viewModel, isWide = false)
        }
    }
}


@Composable
private fun CreateEventForm(
    state: CreateEventState,
    viewModel: CreateEventViewModel,
    isWide: Boolean
) {
    AnimatedVisibility(
        visible = state.generalError != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        state.generalError?.let { error ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }

    OutlinedTextField(
        value = state.title,
        onValueChange = viewModel::onTitleChange,
        label = { Text("Event title") },
        placeholder = { Text("Give your event a name") },
        leadingIcon = { Icon(Icons.Default.Edit, null) },
        isError = state.titleError != null,
        supportingText = state.titleError?.let { { Text(it) } },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )

    OutlinedTextField(
        value = state.description,
        onValueChange = viewModel::onDescriptionChange,
        label = { Text("Description") },
        placeholder = { Text("What's this event about?") },
        minLines = 3,
        maxLines = 5,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )

    OutlinedTextField(
        value = state.locationName,
        onValueChange = viewModel::onLocationNameChange,
        label = { Text("Location") },
        placeholder = { Text("Where will this event take place?") },
        leadingIcon = { Icon(Icons.Default.Place, null) },
        isError = state.locationError != null,
        supportingText = state.locationError?.let { { Text(it) } },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )

    if (isWide) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DateField(
                value = state.date,
                error = state.dateError,
                onValueChange = viewModel::onDateChange,
                modifier = Modifier.weight(1f)
            )
            TimeField(
                value = state.time,
                onValueChange = viewModel::onTimeChange,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        DateField(
            value = state.date,
            error = state.dateError,
            onValueChange = viewModel::onDateChange,
            modifier = Modifier.fillMaxWidth()
        )
        TimeField(
            value = state.time,
            onValueChange = viewModel::onTimeChange,
            modifier = Modifier.fillMaxWidth()
        )
    }

    DurationDropdown(
        selected = state.durationMinutes,
        onChange = viewModel::onDurationChange,
        modifier = Modifier.fillMaxWidth()
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            SwitchRow(
                icon = Icons.Default.Lock,
                label = "Private event",
                description = "Only invited people can see this event",
                checked = state.isPrivate,
                onCheckedChange = viewModel::onPrivateChange
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SwitchRow(
                icon = Icons.Default.AttachMoney,
                label = "Paid event",
                description = "Attendees need to purchase a ticket",
                checked = state.isPaid,
                onCheckedChange = viewModel::onPaidChange
            )
        }
    }

    CoordinatesSection(
        latitude = state.latitude,
        longitude = state.longitude,
        onLatitudeChange = viewModel::onLatitudeChange,
        onLongitudeChange = viewModel::onLongitudeChange,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(8.dp))

    Button(
        onClick = viewModel::submit,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        enabled = state.isValid && !state.isSubmitting,
        shape = RoundedCornerShape(14.dp)
    ) {
        if (state.isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Create Event", style = MaterialTheme.typography.titleMedium)
        }
    }

    Spacer(Modifier.height(24.dp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Box(modifier = modifier.clickable { showPicker = true }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Date") },
            placeholder = { Text("YYYY-MM-DD") },
            leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // epochMillis -> YYYY-MM-DD (UTC)
                        val totalDays = millis / 86_400_000
                        var year = 1970
                        var remaining = totalDays.toInt()
                        while (true) {
                            val daysInYear = if (isLeapYear(year)) 366 else 365
                            if (remaining < daysInYear) break
                            remaining -= daysInYear
                            year++
                        }
                        val monthDays = if (isLeapYear(year))
                            intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
                        else
                            intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
                        var month = 0
                        while (month < 12 && remaining >= monthDays[month]) {
                            remaining -= monthDays[month]
                            month++
                        }
                        val day = remaining + 1
                        onValueChange("${year.toString().padStart(4, '0')}-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}")
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun TimeField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Time") },
        placeholder = { Text("HH:MM") },
        leadingIcon = { Icon(Icons.Default.Schedule, null) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurationDropdown(
    selected: Long,
    onChange: (Long) -> Unit,
    modifier: Modifier = Modifier
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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

@Composable
private fun CoordinatesSection(
    latitude: String,
    longitude: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                    text = "Coordinates (optional)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = onLatitudeChange,
                        label = { Text("Latitude") },
                        placeholder = { Text("e.g. 40.7128") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = onLongitudeChange,
                        label = { Text("Longitude") },
                        placeholder = { Text("e.g. -74.0060") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }
        }
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}
