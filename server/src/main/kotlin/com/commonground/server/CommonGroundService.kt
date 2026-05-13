package com.commonground.server

import com.commonground.core.Event
import com.commonground.core.User
import com.commonground.server.data.EventRepository
import com.commonground.server.data.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommonGroundService(
    private val eventRepo: EventRepository,
    private val userRepo: UserRepository
) {
    fun getAllEvents(): List<Event> {
        return eventRepo.findAll().map { it.toModel() }
    }

    fun getAllUsers(): List<User> {
        return userRepo.findAll().map { it.toModel() }
    }

    fun getEvent(id: UUID): Event {
        return eventRepo.getReferenceById(id).toModel()
    }

    fun getUser(id: UUID): User {
        return userRepo.getReferenceById(id).toModel()
    }

    fun deleteEvent(id: UUID) {
        eventRepo.deleteById(id)
    }

    fun deleteUser(id: UUID) {
        userRepo.deleteById(id)
    }

    private fun com.commonground.server.data.Event.toModel() = Event(
        id = id?.toString(),
        title = title,
        description = description,
        location = location,
        date = date,
        isPrivate = isPrivate,
        durationMinutes = durationMinutes,
        isPaid = isPaid,
        image = image,
        creator = creator.toModel()
    )

    private fun com.commonground.server.data.User.toModel() = User(
        id = id?.toString(),
        username = username,
        displayName = displayName,
        bio = bio,
        emailAddress = emailAddress,
        profilePic = profilePic
    )
}