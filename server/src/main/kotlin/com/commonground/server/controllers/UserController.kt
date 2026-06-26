package com.commonground.server.controllers

import com.commonground.core.models.Events
import com.commonground.core.models.User
import com.commonground.core.models.UserEventType
import com.commonground.core.models.Users
import com.commonground.server.services.EventService
import com.commonground.server.services.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val eventService: EventService
) {
    @GetMapping("/{id}")
    fun user(
        @PathVariable id: String,
        @AuthenticationPrincipal loggedInUserId: String
    ): User? {
        return userService.getUserWithFollowState(id, loggedInUserId)
    }

    @GetMapping("/{id}/followers")
    fun followers(
        @PathVariable id: String,
        @RequestParam pageNumber: Int,
        @AuthenticationPrincipal loggedInUserId: String
    ): Users {
        return userService.getFollowersWithFollowState(
            followeeId = id,
            followStateUserId = loggedInUserId,
            pageNumber = pageNumber
        )
    }

    @GetMapping("/{id}/followees")
    fun followees(
        @PathVariable id: String,
        @RequestParam pageNumber: Int,
        @AuthenticationPrincipal loggedInUserId: String
    ): Users {
        return userService.getFolloweesWithFollowState(
            followerId = id,
            followStateUserId = loggedInUserId,
            pageNumber = pageNumber
        )
    }

    @GetMapping("/{id}/events")
    fun events(
        @PathVariable id: String,
        @RequestParam type: UserEventType,
        @RequestParam pageNumber: Int,
        @AuthenticationPrincipal loggedInUserId: String
    ): Events {
        return eventService.getUserEvents(
            userId = id,
            type = type,
            observerUserId = loggedInUserId,
            pageNumber = pageNumber
        )
    }
}