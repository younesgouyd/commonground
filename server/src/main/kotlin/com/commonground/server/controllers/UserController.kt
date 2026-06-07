package com.commonground.server.controllers

import com.commonground.core.models.ProfileResponse
import com.commonground.server.services.AuthService
import com.commonground.server.services.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val authService: AuthService,
    private val userService: UserService
) {
    @GetMapping("/profile")
    fun profile(): ProfileResponse {
        return userService.getMyProfile()
    }

    @PostMapping("/logout")
    fun logout(@RequestBody refreshToken: String) {
        authService.logout(refreshToken)
    }
}