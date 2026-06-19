package com.commonground.server.controllers

import com.commonground.core.models.CreateEventRequest
import com.commonground.core.models.Event
import com.commonground.core.models.Events
import com.commonground.server.services.EventService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

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
        @RequestParam pageNumber: Int
    ): Events {
        return eventService.getEventsNearLocation(
            latitude = latitude,
            longitude = longitude,
            radiusKilometers = radiusKilometers,
            pageNumber = pageNumber
        )
    }

    @GetMapping("/{id}")
    fun event(@PathVariable id: String): Event? {
        return eventService.getEvent(id)
    }

    @PostMapping
    fun post(
        @AuthenticationPrincipal userId: String,
        @RequestBody request: CreateEventRequest
    ): Event {
        return eventService.createEvent(request, userId)
    }

    @PutMapping("/{id}")
    fun put(@PathVariable id: String, @RequestBody event: Event) {
        // TODO
//        throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        eventService.delete(id)
    }
}