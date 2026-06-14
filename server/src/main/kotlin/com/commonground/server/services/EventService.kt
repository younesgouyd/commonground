package com.commonground.server.services

import com.commonground.core.models.CreateEventRequest
import com.commonground.core.models.Event
import com.commonground.server.data.EventRepository
import com.commonground.server.data.UserRepository
import com.commonground.server.util.GeometryUtils
import com.commonground.server.util.toModel
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createEvent(request: CreateEventRequest): Event {
        val userId = SecurityContextHolder.getContext().authentication?.principal as? String
            ?: throw IllegalStateException("Not authenticated")

        val user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow { NoSuchElementException("User not found: $userId") }

        validateRequest(request)

        val lat = request.latitude
        val lng = request.longitude
        val point = if (lat != null && lng != null) {
            GeometryUtils.createPoint(lat, lng)
        } else {
            GeometryUtils.createPoint(0.0, 0.0)
        }

        val entity = com.commonground.server.data.entities.Event(
            title = request.title.trim(),
            description = request.description?.trim()?.ifBlank { null },
            locationName = request.locationName.trim(),
            coordinates = point,
            date = request.date,
            isPrivate = request.isPrivate,
            durationMinutes = request.durationMinutes,
            isPaid = request.isPaid,
            image = null,
            creator = user
        )

        return eventRepository.save(entity).toModel()
    }

    private fun validateRequest(request: CreateEventRequest) {
        require(request.title.isNotBlank()) { "Title is required" }
        require(request.locationName.isNotBlank()) { "Location is required" }
        //TODO : to be checked
//        require(request.date != null) { "Date is required" }
        require(request.durationMinutes > 0) { "Duration must be positive" }
    }
}
