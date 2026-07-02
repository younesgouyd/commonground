package com.commonground.server.websocket

import com.commonground.core.models.ChatWsMessage
import com.commonground.server.services.ChatService
import com.commonground.server.services.JwtService
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.net.URI
import java.util.*

@Component
class ChatWebSocketHandler(
    private val chatService: ChatService,
    private val jwtService: JwtService
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val uri: URI = session.uri ?: run {
            session.close(CloseStatus.BAD_DATA)
            return
        }

        // Extract eventId from path: /api/v1/ws/chat/{eventId}
        val pathSegments = uri.path.trim('/').split('/')
        val eventId = pathSegments.lastOrNull()?.let { tryParse(it) } ?: run {
            session.close(CloseStatus.BAD_DATA)
            return
        }

        // Extract and validate JWT from query param
        val token = uri.query?.split("&")
            ?.map { it.split("=", limit = 2) }
            ?.firstOrNull { it[0] == "token" }
            ?.getOrNull(1)
        if (token == null || !jwtService.validateAccessToken(token)) {
            session.close(CloseStatus.POLICY_VIOLATION)
            return
        }
        val userId = jwtService.getUserIdFromAccessToken(token)?.let { tryParse(it) } ?: run {
            session.close(CloseStatus.POLICY_VIOLATION)
            return
        }

        chatService.joinRoom(session, eventId, userId)
        chatService.sendHistory(session, eventId)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val wsMessage: ChatWsMessage = try {
            Json.decodeFromString(ChatWsMessage.serializer(), message.payload)
        } catch (_: Exception) {
            return
        }

        val content = wsMessage.content?.trim() ?: return
        if (wsMessage.type != ChatWsMessage.TYPE_MESSAGE || content.isBlank()) {
            return
        }

        val userId = chatService.getSessionUserId(session.id) ?: return
        val eventId = chatService.getSessionEventId(session.id) ?: return

        try {
            chatService.sendMessage(
                eventId = eventId.toString(),
                senderId = userId.toString(),
                content = content
            )
        } catch (_: Exception) {
            val error = ChatWsMessage(type = ChatWsMessage.TYPE_ERROR, content = "Failed to send message")
            val json = Json.encodeToString(ChatWsMessage.serializer(), error)
            session.sendMessage(TextMessage(json))
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        chatService.leaveRoom(session)
    }

    private fun tryParse(uuid: String): UUID? = try {
        UUID.fromString(uuid)
    } catch (_: IllegalArgumentException) {
        null
    }
}
