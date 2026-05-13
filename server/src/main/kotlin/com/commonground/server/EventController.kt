package com.commonground.server

import com.commonground.core.Event
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/events")
class EventController(private val service: CommonGroundService) {
    @GetMapping // shortcut to @RequestMapping(method = [RequestMethod.GET])
    fun events(): List<Event> {
        return service.getAllEvents()
    }

    @GetMapping("/{id}")
    fun event(@PathVariable id: String): Event {
        return service.getEvent(id.toUuid())
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
        service.deleteEvent(id.toUuid())
    }

    private fun String.toUuid() = UUID.fromString(this)
}