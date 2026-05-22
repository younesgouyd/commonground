package com.commonground.server.data

import com.commonground.server.data.entities.Event
import com.commonground.server.data.entities.RefreshToken
import com.commonground.server.data.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EventRepository : JpaRepository<Event, UUID>

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsernameOrEmailAddress(username: String, emailAddress: String): User?
    fun existsByUsernameOrEmailAddress(username: String, emailAddress: String): Boolean
}

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
    fun existsByUserIdAndToken(userId: UUID, token: String): Boolean
    fun deleteByUserIdAndToken(userId: UUID, token: String)
}