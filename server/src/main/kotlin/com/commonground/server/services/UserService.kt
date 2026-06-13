package com.commonground.server.services

import com.commonground.core.models.ProfileResponse
import com.commonground.core.models.UserEvents
import com.commonground.server.data.UserRepository
import com.commonground.server.util.toModel
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun getMyProfile(): ProfileResponse {
        val userEntity = getAuthenticatedUser()
        val user = userEntity.toModel()
        val events = getMyEvents(userEntity)

        return ProfileResponse(
            user = user,
            events = events,
            friendCount = 0, // TODO : get friends not impl yet
            eventCount = events.created.size + events.going.size + events.went.size
        )
    }

    @Transactional(readOnly = true)
    fun getMyEvents(): UserEvents {
        return getMyEvents(getAuthenticatedUser())
    }

    private fun getMyEvents(userEntity: com.commonground.server.data.entities.User): UserEvents {
        val createdEvents = userEntity.createdEvents.map { it.toModel() }
        val goingEvents = userEntity.attendingEvents
            .filter { it.creator.id != userEntity.id }
            .map { it.toModel() }
        val wentEvents = emptyList<com.commonground.core.models.Event>() // TODO: filter by date

        return UserEvents(
            created = createdEvents,
            going = goingEvents,
            went = wentEvents
        )
    }

    private fun getAuthenticatedUser(): com.commonground.server.data.entities.User {
        val userId = SecurityContextHolder.getContext().authentication?.principal as? String
            ?: throw IllegalStateException("Not authenticated")
        return userRepository.findById(UUID.fromString(userId))
            .orElseThrow { NoSuchElementException("User not found: $userId") }
    }
}
