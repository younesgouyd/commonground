package com.commonground.server.services

import com.commonground.core.models.CreateEventRequest
import com.commonground.core.models.Event
import com.commonground.core.models.Events
import com.commonground.core.models.UserEventType
import com.commonground.server.data.repositories.EventRepository
import com.commonground.server.data.repositories.UserRepository
import com.commonground.server.util.GeometryUtils
import com.commonground.server.util.toModel
import com.commonground.server.util.toUuid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Clock
import kotlin.time.toJavaInstant

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createEvent(request: CreateEventRequest, creatorId: String): Event {
        val user = userRepository.getReferenceById(creatorId.toUuid())

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
            date = request.date.toJavaInstant(),
            isPrivate = request.isPrivate,
            durationMinutes = request.durationMinutes,
            isPaid = request.isPaid,
            image = null,
            creator = user
        )

        return eventRepository.save(entity).toModel()
    }

    @Transactional
    fun getEventsNearLocation(
        latitude: Double,
        longitude: Double,
        radiusKilometers: Int,
        pageNumber: Int
    ): Events {
        return eventRepository.findEventsNearLocation(
            location = GeometryUtils.createPoint(latitude, longitude),
            radiusMeters = radiusKilometers * 1000,
            pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE)
        ).map(com.commonground.server.data.entities.Event::toModel)
            .toModel()
    }

    @Transactional
    fun getEvent(id: String): Event? {
        return eventRepository.findById(id.toUuid())
            .getOrNull()
            ?.toModel()
    }

    @Transactional
    fun getUserEvents(
        userId: String,
        type: UserEventType,
        pageNumber: Int
    ): Events {
        val userRef = userRepository.getReferenceById(userId.toUuid())
        return when (type) {
            UserEventType.Created -> {
                eventRepository.findByCreator(
                    creator = userRef,
                    pageable = PageRequest.of(
                        pageNumber,
                        DEFAULT_PAGE_SIZE,
                        Sort.by(
                            Sort.Order.asc("createdAt"),
                            Sort.Order.asc("id")
                        )
                    )
                ).map(com.commonground.server.data.entities.Event::toModel)
                    .toModel()
            }
            UserEventType.Attending -> {
                eventRepository.findUserEventsAfterDate(
                    user = userRef,
                    date = Instant.now(),
                    pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE)
                ).map(com.commonground.server.data.entities.Event::toModel)
                    .toModel()
            }
            UserEventType.Went -> {
                eventRepository.findUserEventsBeforeDate(
                    user = userRef,
                    date = Instant.now(),
                    pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE)
                ).map(com.commonground.server.data.entities.Event::toModel)
                    .toModel()
            }
        }
    }

    @Transactional
    fun delete(id: String) {
        eventRepository.deleteById(id.toUuid())
    }

    private fun validateRequest(request: CreateEventRequest) {
        require(request.title.isNotBlank()) { "Title is required" }
        require(request.locationName.isNotBlank()) { "Location is required" }
        require(request.date > Clock.System.now()) { "The event must be in the future" }
        require(request.durationMinutes > 0) { "Duration must be positive" }
    }

    private fun Slice<Event>.toModel(): Events {
        return Events(
            items = this.content,
            next = if (this.hasNext()) this.nextPageable().pageNumber else null,
            total = null
        )
    }

    private fun Page<Event>.toModel(): Events {
        return Events(
            items = this.content,
            next = if (this.hasNext()) this.nextPageable().pageNumber else null,
            total = this.totalElements
        )
    }
}
