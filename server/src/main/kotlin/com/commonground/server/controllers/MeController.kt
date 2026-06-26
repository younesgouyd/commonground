package com.commonground.server.controllers

import com.commonground.core.models.*
import com.commonground.server.services.AuthService
import com.commonground.server.services.EventService
import com.commonground.server.services.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/me")
class MeController(
    private val authService: AuthService,
    private val userService: UserService,
    private val eventService: EventService
) {
    @GetMapping
    fun me(@AuthenticationPrincipal userId: String): User? {
        return userService.getUserWithFollowState(userId, userId)
    }

    @PatchMapping
    fun me(
        @RequestBody requestBody: UpdateProfileRequest,
        @AuthenticationPrincipal userId: String
    ) {
        userService.update(
            id = userId,
            username = requestBody.username,
            displayName = requestBody.displayName,
            bio = requestBody.bio
        )
    }

    @PatchMapping("/profilePic")
    fun patchProfilePic(
        @RequestParam("file") file: MultipartFile,
        @AuthenticationPrincipal userId: String
    ) {
        userService.updateProfilePic(userId, file)
    }

    @DeleteMapping("/profilePic")
    fun deleteProfilePic(@AuthenticationPrincipal userId: String) {
        userService.clearProfilePic(userId)
    }

    @GetMapping("/followers")
    fun followers(
        @RequestParam pageNumber: Int,
        @AuthenticationPrincipal userId: String
    ): Users {
        return userService.getFollowersWithFollowState(userId, userId, pageNumber)
    }

    @GetMapping("/followees")
    fun followees(
        @RequestParam pageNumber: Int,
        @AuthenticationPrincipal userId: String
    ): Users {
        return userService.getFolloweesWithFollowState(userId, userId, pageNumber)
    }

    @PutMapping("/followees")
    fun follow(
        @RequestParam userId: String,
        @AuthenticationPrincipal loggedInUserId: String
    ) {
        userService.followUser(followerId = loggedInUserId, followeeId =  userId)
    }

    @DeleteMapping("/followees")
    fun unfollow(
        @RequestParam userId: String,
        @AuthenticationPrincipal loggedInUserId: String
    ) {
        userService.unfollowUser(followerId = loggedInUserId, followeeId = userId)
    }

    @GetMapping("/events")
    fun events(
        @RequestParam type: UserEventType,
        @RequestParam pageNumber: Int,
        @AuthenticationPrincipal userId: String
    ): Events {
        return eventService.getUserEvents(
            userId = userId,
            type = type,
            observerUserId = userId,
            pageNumber = pageNumber
        )
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody refreshToken: String,
        @AuthenticationPrincipal userId: String
    ) {
        authService.logout(userId, refreshToken)
    }
}