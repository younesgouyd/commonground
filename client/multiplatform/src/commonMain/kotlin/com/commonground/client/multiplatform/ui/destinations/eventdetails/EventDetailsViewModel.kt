package com.commonground.client.multiplatform.ui.destinations.eventdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.client.multiplatform.data.repositories.AuthRepo
import com.commonground.client.multiplatform.data.repositories.ChatRepo
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.client.multiplatform.data.repositories.UserRepo
import com.commonground.client.multiplatform.ui.formatted
import com.commonground.core.models.ChatWsMessage
import com.commonground.core.models.Event
import com.commonground.core.models.ImageUrl
import com.commonground.core.models.User
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
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
        val isLoggedInUserEvent: Boolean,
        val isBooked: Boolean = false,
        val bookingCount: Int = 12,
        val messages: List<ChatMessage> = emptyList(),
        val newMessage: String = "",
        val isBooking: Boolean = false,
        val updateImage: suspend (ByteArray) -> ImageUrl?
    ) : EventDetailsState()

    data object NotFound : EventDetailsState()

    data class Error(val message: String) : EventDetailsState()
}

class EventDetailsViewModel(
    val id: String,
    private val eventRepo: EventRepo,
    private val userRepo: UserRepo,
    private val authRepo: AuthRepo,
    private val chatRepo: ChatRepo
) : ViewModel() {
    private val logger = KotlinLogging.logger {  }
    private val _state: MutableStateFlow<EventDetailsState> = MutableStateFlow(EventDetailsState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                val event = eventRepo.getEvent(id)
                val loggedInUser = userRepo.getLoggedInUser()
                if (loggedInUser == null) {
                    EventDetailsState.Error("Something went wrong.")
                } else if (event != null) {
                    EventDetailsState.Loaded(
                        event = event,
                        creators = listOf(event.creator),
                        isLoggedInUserEvent = event.creator.id == loggedInUser.id,
                        messages = emptyList(),
                        updateImage = {
                            viewModelScope.async {
                                eventRepo.updateImage(id, it)
                                eventRepo.getEvent(id)?.image
                            }.await()
                        }
                    ).also { connectChat(loggedInUser.id) }
                } else EventDetailsState.NotFound
            } catch (e: Exception) {
                logger.error(e) {}
                EventDetailsState.Error(e.message ?: "Failed to load event details.")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatRepo.close()
    }

    private fun connectChat(loggedInUserId: String) {
        viewModelScope.launch {
            val token = authRepo.loadTokens()?.accessToken ?: run {
                logger.warn { "No access token available for chat connection" }
                return@launch
            }

            try {
                chatRepo.connect(eventId = id, token = token)
                    .collect { wsMessage ->
                        when (wsMessage.type) {
                            ChatWsMessage.TYPE_HISTORY -> {
                                val msgs = wsMessage.messages?.map { it.toLocal(loggedInUserId) } ?: return@collect
                                replaceMessages(msgs)
                            }
                            ChatWsMessage.TYPE_MESSAGE -> {
                                wsMessage.message?.let { appendMessage(it.toLocal(loggedInUserId)) }
                            }
                        }
                    }
            } catch (e: Exception) {
                logger.warn(e) { "Chat flow collection stopped" }
            }
        }
    }

    fun toggleBooking() {
        val s = _state.value as? EventDetailsState.Loaded ?: return
        if (s.isBooking) return
        _state.update { s.copy(isBooking = true) }
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

        _state.update { s.copy(newMessage = "") }
        viewModelScope.launch {
            chatRepo.send(text)
        }
    }

    private fun appendMessage(message: ChatMessage) {
        val s = _state.value as? EventDetailsState.Loaded ?: return
        _state.update { s.copy(messages = s.messages + message) }
    }

    private fun replaceMessages(messages: List<ChatMessage>) {
        val s = _state.value as? EventDetailsState.Loaded ?: return
        _state.update { s.copy(messages = messages) }
    }

    private fun com.commonground.core.models.ChatMessage.toLocal(loggedInUserId: String) = ChatMessage(
        id = id,
        senderName = sender.displayName ?: sender.username,
        content = content,
        timestamp = createdAt.formatted(),
        isOwn = sender.id == loggedInUserId
    )
}
