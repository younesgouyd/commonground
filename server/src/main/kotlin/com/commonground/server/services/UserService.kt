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
        val userId = SecurityContextHolder.getContext().authentication?.principal as? String
            ?: throw IllegalStateException("Not authenticated")
        val userEntity = userRepository.findById(UUID.fromString(userId))
            .orElseThrow { NoSuchElementException("User not found: $userId") }

        val createdEvents = userEntity.createdEvents.map { it.toModel() }
        val goingEvents = userEntity.attendingEvents
            .filter { it.creator.id != userEntity.id }
            .map { it.toModel() }
        val wentEvents = emptyList<com.commonground.core.models.Event>() // TODO: filter by date

        val user = userEntity.toModel()
        val events = UserEvents(
            created = createdEvents,
            going = goingEvents,
            went = wentEvents
        )

        return ProfileResponse(
            user = user,
            events = events,
            friendCount = 0, // TODO : get friends not impl yet
            eventCount = createdEvents.size + goingEvents.size + wentEvents.size
        )
    }
}
