package com.commonground.client.multiplatform.ui.destinations.createevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.core.models.CreateEventRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.random.Random
import kotlin.time.toKotlinInstant

data class CreateEventState(
    val title: String = "",
    val description: String = "",
    val locationName: String = "",
    val date: String = "",
    val time: String = "18:00",
    val durationMinutes: Long = 60,
    val isPrivate: Boolean = false,
    val isPaid: Boolean = false,
    val latitude: String = "",
    val longitude: String = "",
    val isSubmitting: Boolean = false,
    val titleError: String? = null,
    val locationError: String? = null,
    val dateError: String? = null,
    val generalError: String? = null
) {
    val isValid: Boolean
        get() = title.isNotBlank() && locationName.isNotBlank() && date.isNotBlank()
}

class CreateEventViewModel(
    private val eventRepo: EventRepo,
    private val onEventCreated: (String) -> Unit
) : ViewModel() {
    private val _state = MutableStateFlow(CreateEventState())
    val state = _state.asStateFlow()

    fun onTitleChange(v: String) = _state.update { it.copy(title = v, titleError = null, generalError = null) }
    fun onDescriptionChange(v: String) = _state.update { it.copy(description = v, generalError = null) }
    fun onLocationNameChange(v: String) = _state.update { it.copy(locationName = v, locationError = null, generalError = null) }
    fun onDateChange(v: String) = _state.update { it.copy(date = v, dateError = null, generalError = null) }
    fun onTimeChange(v: String) = _state.update { it.copy(time = v, generalError = null) }
    fun onDurationChange(v: Long) = _state.update { it.copy(durationMinutes = v, generalError = null) }
    fun onPrivateChange(v: Boolean) = _state.update { it.copy(isPrivate = v, generalError = null) }
    fun onPaidChange(v: Boolean) = _state.update { it.copy(isPaid = v, generalError = null) }
    fun onLatitudeChange(v: String) = _state.update { it.copy(latitude = v, generalError = null) }
    fun onLongitudeChange(v: String) = _state.update { it.copy(longitude = v, generalError = null) }

    fun submit() {
        val s = _state.value
        if (s.isSubmitting) return

        var titleErr: String? = null
        var locationErr: String? = null
        var dateErr: String? = null

        if (s.title.isBlank()) titleErr = "Title is required"
        if (s.locationName.isBlank()) locationErr = "Location is required"
        if (s.date.isBlank()) dateErr = "Date is required"

        if (titleErr != null || locationErr != null || dateErr != null) {
            _state.update { it.copy(titleError = titleErr, locationError = locationErr, dateError = dateErr) }
            return
        }

        _state.update { it.copy(isSubmitting = true, generalError = null) }
        viewModelScope.launch {
            try {
                val request = CreateEventRequest(
                    title = s.title.trim(),
                    description = s.description.trim().ifBlank { null },
                    locationName = s.locationName.trim(),
                    latitude = s.latitude.toDoubleOrNull(),
                    longitude = s.longitude.toDoubleOrNull(),
                    // TODO
                    date = LocalDateTime.of(2026, 7, Random.nextInt(1, 29), Random.nextInt(10, 22), 0, 0)
                        .toInstant(ZoneOffset.UTC)
                        .toKotlinInstant(), // "${s.date.trim()}T${s.time.trim()}:00",
                    isPrivate = s.isPrivate,
                    durationMinutes = s.durationMinutes,
                    isPaid = s.isPaid
                )
                val created = eventRepo.createEvent(request)
                _state.update { it.copy(isSubmitting = false) }
                onEventCreated(created.id)
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, generalError = e.message ?: "Something went wrong.") }
            }
        }
    }
}
