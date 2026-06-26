package com.commonground.server.services

import com.commonground.core.models.*
import com.commonground.server.data.repositories.EventRepository
import com.commonground.server.data.repositories.UserRepository
import com.commonground.server.util.GeometryUtils
import com.commonground.server.util.GeometryUtils.toPoint
import com.commonground.server.util.toModel
import com.commonground.server.util.toUuid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Clock
import kotlin.time.toJavaInstant

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val imageService: ImageService
) {
    @Transactional
    fun createEvent(request: SaveEventRequest, creatorId: String): Event {
        val user = userRepository.getReferenceById(creatorId.toUuid())

        validateInput(
            title = request.title,
            locationName = request.locationName,
            coordinates = request.coordinates,
            startDate = request.startDate,
            endDate = request.endDate
        )

        val entity = com.commonground.server.data.entities.Event(
            title = request.title.trim(),
            description = request.description?.trim()?.ifBlank { null },
            locationName = request.locationName.trim(),
            coordinates = request.coordinates?.toPoint() ?: GeometryUtils.createPoint(0.0, 0.0),
            startDate = request.startDate.toJavaInstant(),
            endDate = request.endDate?.toJavaInstant(),
            isPrivate = request.isPrivate,
            isPrivatePlace = request.isPrivatePlace,
            isPaid = request.isPaid,
            image = null,
            creator = user
        )

        return eventRepository.save(entity).toModel()
    }

    @Transactional
    fun updateEvent(eventId: String, requestorId: String, request: SaveEventRequest) {
        val requestor = userRepository.findById(requestorId.toUuid()).getOrNull() ?: return
        val event = eventRepository.findById(eventId.toUuid()).getOrNull() ?: return
        if (requestor.id != event.creator.id) {
            return
        }
        validateInput(
            title = request.title,
            locationName = request.locationName,
            coordinates = request.coordinates,
            startDate = request.startDate,
            endDate = request.endDate
        )
        eventRepository.update(
            id = event.id,
            title = request.title.trim(),
            description = request.description?.trim()?.ifBlank { null },
            locationName = request.locationName.trim(),
            coordinates = request.coordinates?.toPoint() ?: GeometryUtils.createPoint(0.0, 0.0),
            startDate = request.startDate.toJavaInstant(),
            endDate = request.endDate?.toJavaInstant(),
            isPrivate = request.isPrivate,
            isPrivatePlace = request.isPrivatePlace,
            isPaid = request.isPaid
        )
    }

    @Transactional
    fun updateImage(eventId: String, requestorId: String, file: MultipartFile) {
        val requestor = userRepository.findById(requestorId.toUuid()).getOrNull() ?: return
        val event = eventRepository.findById(eventId.toUuid()).getOrNull() ?: return
        if (requestor.id != event.creator.id) {
            return
        }
        val imgUrl = imageService.store(file, "event")
        eventRepository.updateImage(eventId.toUuid(), imgUrl)
        event.image?.let {
            imageService.delete(it)
        }
    }

    @Transactional
    fun clearImage(eventId: String, requestorId: String) {
        val requestor = userRepository.findById(requestorId.toUuid()).getOrNull() ?: return
        val event = eventRepository.findById(eventId.toUuid()).getOrNull() ?: return
        if (requestor.id != event.creator.id) {
            return
        }
        event.image?.let { image ->
            imageService.delete(image)
            eventRepository.updateImage(eventId.toUuid(), null)
        }
    }

    @Transactional
    fun getEventsNearLocation(
        observerUserId: String,
        latitude: Double,
        longitude: Double,
        radiusKilometers: Int,
        pageNumber: Int
    ): Events {
        return eventRepository.findEventsNearLocation(
            observerUserId = observerUserId.toUuid(),
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
        observerUserId: String,
        pageNumber: Int
    ): Events {
        val userRef = userRepository.getReferenceById(userId.toUuid())
        val observerRef = userRepository.getReferenceById(observerUserId.toUuid())
        return when (type) {
            UserEventType.Created -> {
                eventRepository.findByCreator(
                    creator = userRef,
                    observer = observerRef,
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
                    observer = observerRef,
                    pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE)
                ).map(com.commonground.server.data.entities.Event::toModel)
                    .toModel()
            }
            UserEventType.Went -> {
                eventRepository.findUserEventsBeforeDate(
                    user = userRef,
                    date = Instant.now(),
                    observer = observerRef,
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

    private fun validateInput(
        title: String,
        locationName: String,
        coordinates: Coordinates?,
        startDate: kotlin.time.Instant,
        endDate: kotlin.time.Instant?
    ) {
        require(title.isNotBlank()) { "Title is required" }
        require(locationName.isNotBlank()) { "Location is required" }
        require(
            coordinates == null
            || (
                coordinates.latitude in -90.0..90.0
                && coordinates.longitude in -180.0..180.0
            )
        ) { "Invalid coordinates" }
        require(startDate > Clock.System.now()) { "The event must be in the future" }
        require(endDate == null || endDate > startDate) { "end date can't be before start date" }
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
