package com.commonground.server.services

import com.commonground.core.models.ChatMessage
import com.commonground.core.models.ChatWsMessage
import com.commonground.server.data.entities.ChatMessageEntity
import com.commonground.server.data.repositories.ChatMessageRepository
import com.commonground.server.data.repositories.EventRepository
import com.commonground.server.data.repositories.UserRepository
import com.commonground.server.util.toModel
import com.commonground.server.util.toUuid
import kotlinx.serialization.json.Json
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.toKotlinInstant

@Service
class ChatService(
    private val chatMessageRepository: ChatMessageRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) {
    private val rooms: ConcurrentHashMap<UUID, MutableSet<WebSocketSession>> = ConcurrentHashMap()
    private val sessionRooms: ConcurrentHashMap<String, UUID> = ConcurrentHashMap()
    private val sessionUsers: ConcurrentHashMap<String, UUID> = ConcurrentHashMap()

    @Transactional
    fun getMessages(eventId: String, pageNumber: Int): List<ChatMessage> {
        return chatMessageRepository.findByEventIdOrderByCreatedAtAsc(
            eventId = eventId.toUuid(),
            pageable = PageRequest.of(pageNumber, 50)
        ).content.map(ChatMessageEntity::toModel)
    }

    @Transactional
    fun sendMessage(eventId: String, senderId: String, content: String): ChatMessage {
        val event = eventRepository.findById(eventId.toUuid()).orElseThrow()
        val sender = userRepository.findById(senderId.toUuid()).orElseThrow()
        val entity = chatMessageRepository.save(
            ChatMessageEntity(
                event = event,
                sender = sender,
                content = content
            )
        )
        val model = ChatMessage(
            id = entity.id.toString(),
            eventId = eventId,
            sender = sender.toModel(),
            content = content,
            createdAt = entity.createdAt.toKotlinInstant()
        )
        broadcast(eventId.toUuid(), ChatWsMessage(type = ChatWsMessage.TYPE_MESSAGE, message = model))
        return model
    }

    fun joinRoom(session: WebSocketSession, eventId: UUID, userId: UUID) {
        rooms.computeIfAbsent(eventId) { ConcurrentHashMap.newKeySet() }.add(session)
        sessionRooms[session.id] = eventId
        sessionUsers[session.id] = userId
    }

    fun leaveRoom(session: WebSocketSession) {
        val eventId = sessionRooms.remove(session.id) ?: return
        sessionUsers.remove(session.id)
        rooms[eventId]?.remove(session)
        if (rooms[eventId].isNullOrEmpty()) {
            rooms.remove(eventId)
        }
    }

    fun sendHistory(session: WebSocketSession, eventId: UUID) {
        val messages = chatMessageRepository.findByEventIdOrderByCreatedAtAsc(
            eventId = eventId,
            pageable = PageRequest.of(0, 100)
        ).content.map(ChatMessageEntity::toModel)
        val payload = ChatWsMessage(type = ChatWsMessage.TYPE_HISTORY, messages = messages)
        val json = Json.encodeToString(ChatWsMessage.serializer(), payload)
        session.sendMessage(TextMessage(json))
    }

    fun getSessionEventId(sessionId: String): UUID? = sessionRooms[sessionId]
    fun getSessionUserId(sessionId: String): UUID? = sessionUsers[sessionId]

    private fun broadcast(eventId: UUID, message: ChatWsMessage) {
        val json = Json.encodeToString(ChatWsMessage.serializer(), message)
        val text = TextMessage(json)
        rooms[eventId]?.forEach { session ->
            if (session.isOpen) {
                session.sendMessage(text)
            }
        }
    }
}
