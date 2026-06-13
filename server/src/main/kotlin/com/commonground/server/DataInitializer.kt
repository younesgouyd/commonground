package com.commonground.server

import com.commonground.server.data.EventRepository
import com.commonground.server.data.UserRepository
import com.commonground.server.data.entities.Event
import com.commonground.server.data.entities.User
import com.commonground.server.util.GeometryUtils
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.PI
import kotlin.math.asin
import kotlin.random.Random

@Service
class DataInitializer(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) {
    fun populateTestData() {
        val passwordEncoder = BCryptPasswordEncoder()
        val defaultEncodedPassword = passwordEncoder.encode("12345678")!!

        val numOfUsers = 1000
        val usersToSave = (1..numOfUsers).map { i ->
            User(
                username = "user_$i",
                password = defaultEncodedPassword,
                emailAddress = "user_$i@example.com",
                displayName = "User $i",
                bio = "Bio details for tester number $i.",
                profilePic = null
            )
        }
        val savedUsers = userRepository.saveAll(usersToSave)

        val totalEvents = 50000
        val batchSize = 500
        for (chunkStart in 1..totalEvents step batchSize) {
            val chunkEnd = minOf(chunkStart + batchSize - 1, totalEvents)
            val batchEvents = (chunkStart..chunkEnd).map { i ->
                val creatorUser = savedUsers.random()
                val randomAttendees = savedUsers
                    .filter { it.id != creatorUser.id }
                    .take(Random.nextInt(0, numOfUsers))
                    .toMutableList()
                Event(
                    title = "Community Event $i",
                    description = "Testing authorization and data isolation mechanics with a random crowd.",
                    locationName = listOf("Casablanca, Morocco", "Online", "Paris, France", "Remote Hub").random(),
                    coordinates = GeometryUtils.createPoint(
                        latitude = asin(Random.nextDouble(-1.0, 1.0)) * (180.0 / PI),
                        longitude = Random.nextDouble(-180.0, 180.0)
                    ),
                    date = LocalDateTime.of(2026, 7, Random.nextInt(1, 29), Random.nextInt(10, 22), 0, 0)
                        .toInstant(ZoneOffset.UTC),
                    isPrivate = Random.nextBoolean(),
                    durationMinutes = listOf(30L, 60L, 90L, 120L, 180L).random(),
                    isPaid = Random.nextBoolean(),
                    image = null,
                    creator = creatorUser,
                    attendees = randomAttendees
                )
            }
            eventRepository.saveAllAndFlush(batchEvents)
        }
    }
}