package com.commonground.server.controllers

import com.commonground.core.models.ChatMessage
import com.commonground.server.services.ChatService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/events")
class ChatController(
    private val chatService: ChatService
) {
    @GetMapping("/{id}/messages")
    fun messages(
        @PathVariable id: String,
        @RequestParam pageNumber: Int
    ): List<ChatMessage> {
        return chatService.getMessages(id, pageNumber)
    }
}
