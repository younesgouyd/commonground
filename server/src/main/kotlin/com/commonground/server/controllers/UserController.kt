package com.commonground.server.controllers

import com.commonground.server.services.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val authService: AuthService
) {
    @PostMapping("/logout")
    fun logout(@RequestBody refreshToken: String) {
        authService.logout(refreshToken)
    }
}