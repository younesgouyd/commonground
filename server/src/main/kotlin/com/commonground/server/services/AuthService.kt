package com.commonground.server.services

import com.commonground.core.models.SignUpResult
import com.commonground.core.models.TokenPair
import com.commonground.server.data.RefreshTokenRepository
import com.commonground.server.data.UserRepository
import com.commonground.server.data.entities.RefreshToken
import com.commonground.server.data.entities.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.util.*

@Service
class AuthService(
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val USERNAME_REGEX = Regex("^[A-Za-z0-9._-]+$")

    @Transactional
    fun signUp(email: String, username: String, password: String): SignUpResult {
        val structuralErrors = buildList {
            if (email.isNotBlank() && !EMAIL_REGEX.matches(email.trim())) { add(SignUpResult.Error.InvalidEmailAddress) }
            if (username.length < 3 || !USERNAME_REGEX.matches(username)) { add(SignUpResult.Error.InvalidUsername) }
            if (password.length < 8 || !password.any { it.isDigit() } || !password.any { it.isLetter() }) { add(SignUpResult.Error.InvalidPassword) }
        }

        if (structuralErrors.isNotEmpty()) {
            return SignUpResult(structuralErrors, null)
        }

        val databaseErrors = buildList {
            if (userRepository.existsByUsernameOrEmailAddress(username, email)) {
                add(SignUpResult.Error.UsernameOrEmailTaken)
            }
        }

        if (databaseErrors.isNotEmpty()) {
            return SignUpResult(databaseErrors, null)
        }

        val user = User(
            emailAddress = email,
            username = username,
            password = passwordEncoder.encode(password)!!
        )
        userRepository.save(user)
        val token = jwtService.generateTokenPair(user.id.toString())
        saveRefreshToken(token.refreshToken, user.id)
        return SignUpResult(emptyList(), token)
    }

    @Transactional
    fun login(login: String, password: String): TokenPair? {
        val user = userRepository.findByUsernameOrEmailAddress(login, login) ?: return null
        if (!passwordEncoder.matches(password, user.password)) {
            return null
        }
        val token = jwtService.generateTokenPair(user.id.toString())
        saveRefreshToken(token.refreshToken, user.id)
        return token
    }

    @Transactional
    fun refreshToken(refreshToken: String): TokenPair? {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            return null
        }
        val userId = UUID.fromString(jwtService.getUserIdFromRefreshToken(refreshToken))
        if (!userRepository.existsById(userId)) {
            return null
        }
        val hashed = hashToken(refreshToken)
        if (!refreshTokenRepository.existsByUserIdAndToken(userId, hashed)) {
            return null
        }
        refreshTokenRepository.deleteByUserIdAndToken(userId, hashed)
        val token = jwtService.generateTokenPair(userId.toString())
        saveRefreshToken(token.refreshToken, userId)
        return TokenPair(
            accessToken = token.accessToken,
            refreshToken = token.refreshToken
        )
    }

    private fun saveRefreshToken(token: String, userId: UUID) {
        refreshTokenRepository.save(
            RefreshToken(
                token = hashToken(token),
                user = userRepository.getReferenceById(userId)
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}