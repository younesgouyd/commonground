package com.commonground.server.data

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EventRepository : JpaRepository<Event, UUID>

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}