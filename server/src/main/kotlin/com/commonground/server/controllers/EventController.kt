package com.commonground.server.controllers

import com.commonground.core.models.Event
import com.commonground.core.models.Events
import com.commonground.server.data.EventRepository
import com.commonground.server.util.GeometryUtils
import com.commonground.server.util.toModel
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val repo: EventRepository
) {
    @GetMapping
    fun getEventsNearLocation(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam radiusKilometers: Int,
        @RequestParam pageNumber: Int
    ): Events {
        val slice = repo.findEventsNearLocation(
            location = GeometryUtils.createPoint(latitude, longitude),
            radiusMeters = radiusKilometers * 1000,
            pageable = PageRequest.of(pageNumber, 50)
        ).map(com.commonground.server.data.entities.Event::toModel)
        return Events(
            items = slice.content,
            next = if (slice.hasNext()) slice.nextPageable().pageNumber else null
        )
    }

    @GetMapping("/{id}")
    fun event(@PathVariable id: String): ResponseEntity<Event> {
        return repo.findById(id.toUuid()).getOrNull()?.let { ResponseEntity.ok(it.toModel()) } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun post(@RequestBody event: Event) {
        // TODO
    }

    @PutMapping("/{id}")
    fun put(@PathVariable id: String, @RequestBody event: Event) {
        // TODO
//        throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        repo.deleteById(id.toUuid())
    }

    private fun String.toUuid() = UUID.fromString(this)
}