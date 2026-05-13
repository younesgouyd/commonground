package com.commonground.server

import com.commonground.server.data.Event
import com.commonground.server.data.EventRepository
import com.commonground.server.data.User
import com.commonground.server.data.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
class commonGroundApplication {

    // FOR TESTING. comment after first run
    @Bean
    fun runner(dataInitializer: DataInitializer) = CommandLineRunner {
        dataInitializer.populateTestData()
    }

}

fun main(args: Array<String>) {
    runApplication<commonGroundApplication>(*args)
}

// THIS IS FOR TESTING ONLY
// TODO: delete
@Service
class DataInitializer(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) {
    @Transactional
    fun populateTestData() {
        // 1. Create a Creator
        val creator = User(
            username = "neo",
            displayName = "Neo",
            bio = "Developing CommonGround",
            emailAddress = "neo@example.com",
            profilePic = null
        ).let { userRepository.save(it) }

        // 2. Create an Attendee
        val attendee = User(
            username = "tester_alpha",
            displayName = "Alpha Tester",
            bio = "I love events",
            emailAddress = "alpha@example.com",
            profilePic = null
        ).let { userRepository.save(it) }

        // 3. Create Events
        val techMeetup = Event(
            title = "Kotlin Multiplatform Meetup",
            description = "Discussing KMP and AOSP architecture.",
            location = "Casablanca, Morocco",
            date = "2026-06-15T18:00:00",
            isPrivate = false,
            durationMinutes = 120,
            isPaid = false,
            image = null,
            creator = creator,
            attendees = mutableListOf(attendee)
        )

        val privateWorkshop = Event(
            title = "Spring Boot Deep Dive",
            description = "Private session on JPA and Hibernate.",
            location = "Online",
            date = "2026-06-20T10:00:00",
            isPrivate = true,
            durationMinutes = 180,
            isPaid = true,
            image = null,
            creator = creator
        )

        eventRepository.saveAll(listOf(techMeetup, privateWorkshop))
    }
}