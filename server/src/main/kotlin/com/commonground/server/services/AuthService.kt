package com.commonground.server.services

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
    @Transactional
    fun signUp(username: String, password: String): TokenPair? {
        if (userRepository.existsByUsername(username)) {
            return null
        }
        val user = User(
            username = username,
            password = passwordEncoder.encode(password)!!
        )
        userRepository.save(user)
        val token = jwtService.generateTokenPair(user.id.toString())
        saveRefreshToken(token.refreshToken, user.id)
        return token
    }

    @Transactional
    fun login(username: String, password: String): TokenPair? {
        val user = userRepository.findByUsername(username) ?: return null
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