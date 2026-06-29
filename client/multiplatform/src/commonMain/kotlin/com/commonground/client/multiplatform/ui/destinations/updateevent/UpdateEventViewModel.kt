package com.commonground.client.multiplatform.ui.destinations.updateevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.core.models.Coordinates
import com.commonground.core.models.Event
import com.commonground.core.models.ImageUrl
import com.commonground.core.models.SaveEventRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Instant

sealed class UpdateEventState {
    data object Loading : UpdateEventState()

    data class Loaded(
        val event: Event,
        val isSubmitting: StateFlow<Boolean>,
        val updateImage: suspend (ByteArray) -> ImageUrl?,
        val clearImage: () -> Unit,
        val onSubmit: (
            title: String,
            description: String,
            locationName: String,
            coordinates: Coordinates,
            startDate: Instant,
            endDate: Instant?,
            isPrivate: Boolean,
            isPrivatePlace: Boolean,
            isPaid: Boolean
        ) -> Unit
    ) : UpdateEventState()

    data object NotFound : UpdateEventState()

    data object Error : UpdateEventState()
}

class UpdateEventViewModel(
    private val id: String,
    private val eventRepo: EventRepo,
    private val onDone: () -> Unit
) : ViewModel() {
    private val _state: MutableStateFlow<UpdateEventState> = MutableStateFlow(UpdateEventState.Loading)
    private val isSubmitting: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val event = eventRepo.getEvent(id)
            _state.value = if (event == null) {
                UpdateEventState.NotFound
            } else {
                UpdateEventState.Loaded(
                    event = event,
                    isSubmitting = isSubmitting.asStateFlow(),
                    updateImage = ::updateImage,
                    clearImage = ::clearImage,
                    onSubmit = ::submit
                )
            }
        }
    }

    private suspend fun updateImage(value: ByteArray): ImageUrl? {
        return viewModelScope.async {
            eventRepo.updateImage(id, value)
            eventRepo.getEvent(id)?.image
        }.await()
    }

    private fun clearImage() {
        viewModelScope.launch {
            eventRepo.clearImage(id)
        }
    }

    private fun submit(
        title: String,
        description: String,
        locationName: String,
        coordinates: Coordinates,
        startDate: Instant,
        endDate: Instant?,
        isPrivate: Boolean,
        isPrivatePlace: Boolean,
        isPaid: Boolean
    ) {
        viewModelScope.launch {
            isSubmitting.value = true
            try {
                eventRepo.updateEvent(
                    id = id,
                    request = SaveEventRequest(
                        title = title.trim(),
                        description = description.trim().ifBlank { null },
                        locationName = locationName.trim(),
                        coordinates = coordinates,
                        startDate = startDate,
                        endDate = endDate,
                        isPrivate = isPrivate,
                        isPrivatePlace = isPrivatePlace,
                        isPaid = isPaid
                    )
                )
                onDone()
            } catch (_: Exception) {
                _state.value = UpdateEventState.Error
            } finally {
                isSubmitting.value = false
            }
        }
    }
}
