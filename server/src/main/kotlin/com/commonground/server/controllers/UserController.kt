package com.commonground.server.controllers

import com.commonground.core.models.Events
import com.commonground.core.models.User
import com.commonground.core.models.UserEventType
import com.commonground.server.services.EventService
import com.commonground.server.services.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val eventService: EventService
) {
    @GetMapping("/{id}")
    fun user(@PathVariable id: String): User? {
        return userService.getUser(id)
    }

    @GetMapping("/{id}/events")
    fun events(
        @PathVariable id: String,
        @RequestParam type: UserEventType,
        @RequestParam pageNumber: Int
    ): Events {
        return eventService.getUserEvents(id, type, pageNumber)
    }
}