package com.commonground.server.controllers

import com.commonground.core.models.CreateEventRequest
import com.commonground.core.models.Event
import com.commonground.core.models.Events
import com.commonground.core.models.SaveEventRequest
import com.commonground.core.models.Users
import com.commonground.server.services.EventService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val eventService: EventService
) {
    @GetMapping
    fun events(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam radiusKilometers: Int,
        @RequestParam(required = false) isPrivate: Boolean?,
        @RequestParam(required = false) isPrivatePlace: Boolean?,
        @RequestParam(required = false) isPaid: Boolean?,
        @RequestParam(required = false) title: String?,
        @RequestParam pageNumber: Int,
        @AuthenticationPrincipal observerUserId: String
    ): Events {
        return eventService.getEventsNearLocation(
            observerUserId = observerUserId,
            latitude = latitude,
            longitude = longitude,
            radiusKilometers = radiusKilometers,
            isPrivate = isPrivate,
            isPrivatePlace = isPrivatePlace,
            isPaid = isPaid,
            title = title,
            pageNumber = pageNumber
        )
    }

    @GetMapping("/{id}")
    fun event(@PathVariable id: String): Event? {
        return eventService.getEvent(id)
    }

    @PostMapping
    fun post(
        @RequestBody request: CreateEventRequest,
        @AuthenticationPrincipal userId: String
    ): Event {
        return eventService.createEvent(request, userId)
    }

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: String,
        @RequestBody request: SaveEventRequest,
        @AuthenticationPrincipal userId: String
    ) {
        eventService.updateEvent(id, userId, request)
    }

    @PatchMapping("/{id}/image")
    fun patchImage(
        @PathVariable id: String,
        @RequestParam("file") file: MultipartFile,
        @AuthenticationPrincipal userId: String
    ) {
        eventService.updateImage(id, userId, file)
    }

    @DeleteMapping("/{id}/image")
    fun deleteImage(
        @PathVariable id: String,
        @AuthenticationPrincipal userId: String
    ) {
        eventService.clearImage(id, userId)
    }

    @GetMapping("/{id}/attendees")
    fun attendees(
        @PathVariable id: String,
        @RequestParam pageNumber: Int,
        @AuthenticationPrincipal observerUserId: String
    ): Users {
        return eventService.getAttendees(id, observerUserId, pageNumber)
    }

    @PostMapping("/{id}/book")
    fun book(
        @PathVariable id: String,
        @AuthenticationPrincipal userId: String
    ) {
        eventService.bookEvent(id, userId)
    }

    @DeleteMapping("/{id}/book")
    fun unbook(
        @PathVariable id: String,
        @AuthenticationPrincipal userId: String
    ) {
        eventService.unbookEvent(id, userId)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        eventService.delete(id)
    }
}