package com.commonground.server.data.repositories

import com.commonground.server.data.entities.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
    fun existsByUserIdAndToken(userId: UUID, token: String): Boolean
    fun deleteByUserIdAndToken(userId: UUID, token: String)
}