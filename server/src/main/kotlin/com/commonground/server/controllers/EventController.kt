package com.commonground.server.controllers

import com.commonground.core.models.Event
import com.commonground.server.data.EventRepository
import com.commonground.server.toModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val repo: EventRepository
) {
    @GetMapping // shortcut to @RequestMapping(method = [RequestMethod.GET])
    fun events(): List<Event> {
        return repo.findAll().map { it.toModel() }
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