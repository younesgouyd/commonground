package com.commonground.client.multiplatform.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.commonground.client.multiplatform.ui.formatted
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun DateTimeField(
    modifier: Modifier = Modifier,
    label: String,
    value: Instant?,
    error: String?,
    onValueChange: (Instant?) -> Unit
) {
    val datetime = value?.toLocalDateTime(TimeZone.UTC)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                if (value != null) {
                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = { onValueChange(null) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            AnimatedVisibility(error != null) {
                if (error != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = error,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            if (datetime == null) {
                TextButton(
                    onClick = { onValueChange(Clock.System.now()) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Set date and time")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DateField(
                        modifier = Modifier.weight(1f),
                        value = datetime.date,
                        onValueChange = { onValueChange(it.atTime(datetime.time).toInstant(TimeZone.UTC)) }
                    )
                    TimeField(
                        modifier = Modifier.weight(1f),
                        value = datetime.time,
                        onValueChange = { onValueChange(it.atDate(datetime.date).toInstant(TimeZone.UTC)) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    modifier: Modifier = Modifier,
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val dateMillis = value.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateMillis,
        initialDisplayedMonthMillis = dateMillis
    )

    ClickableTextField(
        modifier = modifier,
        value = value.formatted(),
        label = "Date",
        placeholder = "YYYY-MM-DD",
        leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
        onClick = { showPicker = true }
    )

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    content = { Text("OK") },
                    onClick = {
                        val selectedDate = pickerState.selectedDateMillis?.let { Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date }
                        if (selectedDate != null) {
                            onValueChange(selectedDate)
                        }
                        showPicker = false
                    }
                )
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(pickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeField(
    modifier: Modifier = Modifier,
    value: LocalTime,
    onValueChange: (LocalTime) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val pickerState = rememberTimePickerState(
        initialHour = value.hour,
        initialMinute = value.minute,
        is24Hour = true
    )

    ClickableTextField(
        modifier = modifier,
        value = value.formatted(),
        label = "Time",
        placeholder = "HH:MM",
        leadingIcon = { Icon(Icons.Default.Schedule, null) },
        onClick = { showPicker = true }
    )

    if (showPicker) {
        TimePickerDialog(
            title = { Text("Time") },
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    content = { Text("OK") },
                    onClick = {
                        onValueChange(LocalTime(hour = pickerState.hour, minute = pickerState.minute))
                        showPicker = false
                    }
                )
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) {
            TimePicker(
                state = pickerState,
                layoutType = TimePickerLayoutType.Vertical
            )
        }
    }
}

@Composable
private fun ClickableTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val fieldShape = RoundedCornerShape(12.dp)

    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = leadingIcon,
            singleLine = true,
            shape = fieldShape
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(fieldShape) // Restricts the ripple to the 12.dp boundary
                .clickable(onClick = onClick)
        )
    }
}
