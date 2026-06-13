package com.commonground.client.multiplatform.ui.destinations.eventdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.core.models.Event
import com.commonground.core.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val senderName: String,
    val content: String,
    val timestamp: String,
    val isOwn: Boolean
)

sealed class EventDetailsState {
    data object Loading : EventDetailsState()

    data class Loaded(
        val event: Event,
        val creators: List<User>,
        val isBooked: Boolean = false,
        val bookingCount: Int = 12,
        val messages: List<ChatMessage> = emptyList(),
        val newMessage: String = "",
        val isBooking: Boolean = false
    ) : EventDetailsState()

    data object NotFound : EventDetailsState()

    data class Error(val message: String) : EventDetailsState()
}

// ── ViewModel ──────────────────────────────────────────────────────

class EventDetailsViewModel(
    val id: String,
    private val eventRepo: EventRepo
) : ViewModel() {
    private val _state: MutableStateFlow<EventDetailsState> = MutableStateFlow(EventDetailsState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                val event = eventRepo.getEvent(id)
                EventDetailsState.Loaded(
                    event = event,
                    creators = listOf(event.creator),
                    messages = generateMockMessages()
                )
            } catch (e: NoSuchElementException) {
                EventDetailsState.NotFound
            } catch (e: Exception) {
                EventDetailsState.Error(e.message ?: "Failed to load event details.")
            }
        }
    }

    fun toggleBooking() {
        val s = _state.value as? EventDetailsState.Loaded ?: return
        if (s.isBooking) return
        _state.update {
            s.copy(
                isBooking = true
            )
        }
        viewModelScope.launch {
            kotlinx.coroutines.delay(600)
            _state.update {
                s.copy(
                    isBooked = !s.isBooked,
                    bookingCount = if (s.isBooked) s.bookingCount - 1 else s.bookingCount + 1,
                    isBooking = false
                )
            }
        }
    }

    fun onNewMessageChange(value: String) {
        val s = _state.value as? EventDetailsState.Loaded ?: return
        _state.update { s.copy(newMessage = value) }
    }

    fun sendMessage() {
        val s = _state.value as? EventDetailsState.Loaded ?: return
        val text = s.newMessage.trim()
        if (text.isBlank()) return

        val newMsg = ChatMessage(
            id = "msg_${s.messages.size + 1}",
            senderName = "You",
            content = text,
            timestamp = "Just now",
            isOwn = true
        )
        _state.update {
            s.copy(
                messages = s.messages + newMsg,
                newMessage = ""
            )
        }
    }

    private fun generateMockMessages(): List<ChatMessage> = listOf(
        ChatMessage("1", "Alice", "Hey everyone! Excited for this event 🎉", "10:30 AM", false),
        ChatMessage("2", "Bob", "Same here! What time should we meet?", "10:32 AM", false),
        ChatMessage("3", "Alice", "The event starts at 6 PM, let's meet 15 min early", "10:33 AM", false),
        ChatMessage("4", "Charlie", "Sounds good! I'll bring snacks", "10:35 AM", false),
        ChatMessage("5", "Bob", "Perfect, see you all there!", "10:36 AM", false)
    )
}
