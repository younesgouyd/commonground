package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.ChatWsMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json

class ChatRepo(
    private val serverHost: String,
    private val serverPort: Int
) {
    private val logger = KotlinLogging.logger {}
    private val client = HttpClient {
        install(WebSockets)
    }
    private var scope: CoroutineScope? = null
    private var session: WebSocketSession? = null
    private val _messages = MutableSharedFlow<ChatWsMessage>(extraBufferCapacity = 64)

    //TODO: tobeChecked open webS and return the message protocol
    fun connect(eventId: String, token: String): Flow<ChatWsMessage> {
        close() // clean up any previous connection
        val newScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        scope = newScope
        newScope.launch {
            try {
                client.webSocket(
                    host = serverHost,
                    port = serverPort,
                    path = "/api/v1/ws/chat/$eventId?token=$token"
                ) {
                    session = this
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            try {
                                val msg = Json.decodeFromString<ChatWsMessage>(frame.readText())
                                _messages.emit(msg)
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logger.warn(e) { "Chat WebSocket disconnected" }
            }
        }
        return _messages.asSharedFlow()
    }

    suspend fun send(content: String) {
        try {
            val msg = ChatWsMessage(type = ChatWsMessage.TYPE_MESSAGE, content = content)
            val json = Json.encodeToString(ChatWsMessage.serializer(), msg)
            session?.send(Frame.Text(json))
        } catch (e: Exception) {
            logger.warn(e) { "Failed to send chat message" }
        }
    }

    fun close() {
        scope?.cancel()
        scope = null
        session = null
    }
}
