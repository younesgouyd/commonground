package com.commonground.server.services

import com.commonground.core.models.TokenPair
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.access.secret}") private val jwtAccessSecret: String,
    @Value("\${jwt.refresh.secret}") private val jwtRefreshSecret: String
) {
    companion object {
        private const val TOKEN_EXP_MS: Long = 3600000 // 1 hour
        private const val REFRESH_TOKEN_EXP_MS: Long = 2592000000 // 30 days
    }

    private val accessKey: SecretKey by lazy {
        val keyBytes = Decoders.BASE64.decode(jwtAccessSecret)
        Keys.hmacShaKeyFor(keyBytes)
    }

    private val refreshKey: SecretKey by lazy {
        val keyBytes = Decoders.BASE64.decode(jwtRefreshSecret)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateTokenPair(userId: String): TokenPair {
        return TokenPair(
            accessToken = generateAccessToken(userId),
            refreshToken = generateRefreshToken(userId)
        )
    }

    fun validateAccessToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token)
                .payload
            true
        } catch (e: Exception) {
            false
        }
    }

    fun validateRefreshToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(refreshKey)
                .build()
                .parseSignedClaims(token)
                .payload
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromAccessToken(token: String): String? {
        return Jwts.parser()
            .verifyWith(accessKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    fun getUserIdFromRefreshToken(token: String): String? {
        return Jwts.parser()
            .verifyWith(refreshKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    private fun generateAccessToken(userId: String): String {
        return Jwts.builder()
            .subject(userId)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + TOKEN_EXP_MS))
            .signWith(accessKey)
            .compact()
    }

    private fun generateRefreshToken(userId: String): String {
        return Jwts.builder()
            .subject(userId)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + TOKEN_EXP_MS))
            .signWith(refreshKey)
            .compact()
    }
}