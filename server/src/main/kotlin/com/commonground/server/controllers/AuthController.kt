package com.commonground.server.controllers

import com.commonground.core.models.LoginRequest
import com.commonground.core.models.SignUp
import com.commonground.server.data.User
import com.commonground.server.data.UserRepository
import com.commonground.server.services.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUp): ResponseEntity<String> {
        if (userRepository.existsByUsername(request.username)) {
            return ResponseEntity.status(409).body("This username is taken")
        }
        val hashedPassword = passwordEncoder.encode(request.password)!!
        val user = User(
            username = request.username,
            password = hashedPassword
        )
        userRepository.save(user)
        val token = jwtService.generateToken(user.username)
        return ResponseEntity.ok(token)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<String> {
        val user = userRepository.findByUsername(request.username) ?: return ResponseEntity.status(401).body("Invalid username or password")
        if (!passwordEncoder.matches(request.password, user.password)) {
            return ResponseEntity.status(401).body("Invalid username or password")
        }
        val token = jwtService.generateToken(user.username)
        return ResponseEntity.ok(token)
    }
}