package com.commonground.client.multiplatform.ui.destinations.createevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.core.models.Coordinates
import com.commonground.core.models.CreateEventRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.time.Instant

data class CreateEventState(
    val isSubmitting: StateFlow<Boolean>,
    val onSubmit: (
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
)

class CreateEventViewModel(
    private val eventRepo: EventRepo,
    private val onEventCreated: (id: String) -> Unit
) : ViewModel() {
    private val isSubmitting: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _state: MutableStateFlow<CreateEventState> = MutableStateFlow(
        CreateEventState(
            isSubmitting = isSubmitting.asStateFlow(),
            onSubmit = ::submit
        )
    )
    val state = _state.asStateFlow()

    private fun submit(
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
    ) {
        viewModelScope.launch {
            isSubmitting.value = true
            try {
                val event = eventRepo.createEvent(
                    request = CreateEventRequest(
                        title = title.trim(),
                        description = description.trim().ifBlank { null },
                        locationName = locationName.trim(),
                        coordinates = coordinates,
                        startDate = startDate,
                        endDate = endDate,
                        isPrivate = isPrivate,
                        isPrivatePlace = isPrivatePlace,
                        isPaid = isPaid,
                        image = image?.let { Base64.encode(it) }
                    )
                )
                onEventCreated(event.id)
            } finally {
                isSubmitting.value = false
            }
        }
    }
}
