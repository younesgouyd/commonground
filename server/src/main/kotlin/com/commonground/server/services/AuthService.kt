package com.commonground.server.services

import com.commonground.core.models.LoginResult
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
    private val EMAIL_REGEX by lazy { Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") }
    private val USERNAME_REGEX by lazy { Regex("^[A-Za-z0-9._-]+$") }

    @Transactional
    fun signUp(email: String, username: String, password: String): SignUpResult {
        val email = email.trim().ifBlank { null }
        val username = username.trim().ifBlank { null }
        val password = password.ifBlank { null }

        val structuralErrors = buildList {
            if (email != null && !EMAIL_REGEX.matches(email)) { add(SignUpResult.Failure.Error.InvalidEmailAddress) }
            if (username.isNullOrBlank() || username.length < 3 || !USERNAME_REGEX.matches(username)) { add(SignUpResult.Failure.Error.InvalidUsername) }
            if (password.isNullOrBlank() || password.length < 8 || !password.any { it.isDigit() } || !password.any { it.isLetter() }) { add(SignUpResult.Failure.Error.InvalidPassword) }
        }
        if (structuralErrors.isNotEmpty()) {
            return SignUpResult.Failure(structuralErrors)
        }

        username!!
        password!!

        val databaseErrors = buildList {
            if (userRepository.existsByUsername(username)) {
                add(SignUpResult.Failure.Error.UsernameTaken)
            }
            if (email != null && userRepository.existsByEmailAddress(email)) {
                add(SignUpResult.Failure.Error.EmailTaken)
            }
        }

        if (databaseErrors.isNotEmpty()) {
            return SignUpResult.Failure(databaseErrors)
        }

        val user = User(
            username = username,
            password = passwordEncoder.encode(password)!!,
            emailAddress = email
        )
        userRepository.save(user)
        val token = jwtService.generateTokenPair(user.id.toString())
        saveRefreshToken(token.refreshToken, user.id)
        return SignUpResult.Success(token)
    }

    @Transactional
    fun login(login: String, password: String): LoginResult {
        val user = userRepository.findByUsernameOrEmailAddress(login, login) ?: return LoginResult.InvalidCredentials
        if (!passwordEncoder.matches(password, user.password)) {
            return LoginResult.InvalidCredentials
        }
        val tokens = jwtService.generateTokenPair(user.id.toString())
        saveRefreshToken(tokens.refreshToken, user.id)
        return LoginResult.Success(tokens)
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

    @Transactional
    fun logout(refreshToken: String) {
        val userId = UUID.fromString(jwtService.getUserIdFromRefreshToken(refreshToken)!!)
        val hashed = hashToken(refreshToken)
        refreshTokenRepository.deleteByUserIdAndToken(userId, hashed)
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