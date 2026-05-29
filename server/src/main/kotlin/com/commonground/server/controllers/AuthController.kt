package com.commonground.server.controllers

import com.commonground.core.models.*
import com.commonground.server.services.AuthService
import org.springframework.http.HttpStatusCode
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): SignUpResult {
        return authService.signUp(request.email, request.username, request.password)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResult {
        return authService.login(request.login, request.password)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody refreshToken: String): TokenPair {
        return authService.refreshToken(refreshToken)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid refresh token.")
    }
}