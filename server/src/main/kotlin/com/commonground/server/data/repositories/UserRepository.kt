package com.commonground.server.data.repositories

import com.commonground.server.data.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsernameOrEmailAddress(username: String, emailAddress: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmailAddress(emailAddress: String): Boolean
}