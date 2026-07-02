package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatWsMessage(
    val type: String,
    val content: String? = null,
    val message: ChatMessage? = null,
    val messages: List<ChatMessage>? = null
) {
    companion object {
        const val TYPE_MESSAGE = "message"
        const val TYPE_HISTORY = "history"
        const val TYPE_ERROR = "error"
    }
}
