package com.commonground.server.controllers

import com.commonground.core.models.*
import com.commonground.server.services.AuthService
import com.commonground.server.services.EventService
import com.commonground.server.services.ImageService
import com.commonground.server.services.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/me")
class MeController(
    private val authService: AuthService,
    private val userService: UserService,
    private val eventService: EventService,
    private val imageService: ImageService
) {
    @GetMapping
    fun me(@AuthenticationPrincipal userId: String): User? {
        return userService.getUserWithFollowState(userId, userId)
    }

    @PatchMapping
    fun me(
        @AuthenticationPrincipal userId: String,
        @RequestBody requestBody: UpdateProfileRequest
    ) {
        userService.update(
            id = userId,
            username = requestBody.username,
            displayName = requestBody.displayName,
            bio = requestBody.bio
        )
    }

    @PatchMapping("/profilePic")
    fun me(
        @AuthenticationPrincipal userId: String,
        @RequestParam("file") file: MultipartFile
    ) {
        val imgUrl = imageService.store(file, "profile_pic")
        userService.updateProfilePic(userId, imgUrl)
    }

    @GetMapping("/followers")
    fun followers(
        @AuthenticationPrincipal userId: String,
        @RequestParam pageNumber: Int
    ): Users {
        return userService.getFollowersWithFollowState(userId, userId, pageNumber)
    }

    @GetMapping("/followees")
    fun followees(
        @AuthenticationPrincipal userId: String,
        @RequestParam pageNumber: Int
    ): Users {
        return userService.getFolloweesWithFollowState(userId, userId, pageNumber)
    }

    @PutMapping("/followees")
    fun follow(
        @AuthenticationPrincipal loggedInUserId: String,
        @RequestParam userId: String
    ) {
        userService.followUser(followerId = loggedInUserId, followeeId =  userId)
    }

    @DeleteMapping("/followees")
    fun unfollow(
        @AuthenticationPrincipal loggedInUserId: String,
        @RequestParam userId: String
    ) {
        userService.unfollowUser(followerId = loggedInUserId, followeeId = userId)
    }

    @GetMapping("/events")
    fun events(
        @AuthenticationPrincipal userId: String,
        @RequestParam type: UserEventType,
        @RequestParam pageNumber: Int
    ): Events {
        return eventService.getUserEvents(userId, type, pageNumber)
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal userId: String,
        @RequestBody refreshToken: String
    ) {
        authService.logout(userId, refreshToken)
    }
}