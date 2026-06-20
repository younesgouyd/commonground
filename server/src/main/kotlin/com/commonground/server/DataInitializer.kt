package com.commonground.server

import com.commonground.server.data.entities.Event
import com.commonground.server.data.entities.User
import com.commonground.server.data.entities.UserEvent
import com.commonground.server.data.entities.UserFollow
import com.commonground.server.data.repositories.EventRepository
import com.commonground.server.data.repositories.UserEventRepository
import com.commonground.server.data.repositories.UserFollowRepository
import com.commonground.server.data.repositories.UserRepository
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
    private val eventRepository: EventRepository,
    private val userEventRepository: UserEventRepository,
    private val userFollowRepository: UserFollowRepository
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

        // --- Generate Random Follows ---
        val allFollows = mutableListOf<UserFollow>()
        savedUsers.forEach { follower ->
            val followCount = Random.nextInt(0, 50)
            val randomlyFollowed = mutableSetOf<User>()
            while (randomlyFollowed.size < followCount) {
                val followed = savedUsers.random()
                if (followed.id != follower.id) {
                    randomlyFollowed.add(followed)
                }
            }
            randomlyFollowed.forEach { followee ->
                allFollows.add(UserFollow(follower = follower, followee = followee))
            }
        }
        allFollows.chunked(1000).forEach { batch ->
            userFollowRepository.saveAllAndFlush(batch)
        }
        // -------------------------------

        val totalEvents = 50000
        val batchSize = 500
        for (chunkStart in 1..totalEvents step batchSize) {
            val chunkEnd = minOf(chunkStart + batchSize - 1, totalEvents)
            val batchEvents = (chunkStart..chunkEnd).map { i ->
                val creatorUser = savedUsers.random()
                val attendeeCount = Random.nextInt(0, 50)
                val randomAttendees = mutableSetOf<User>()
                while (randomAttendees.size < attendeeCount) {
                    val attendee = savedUsers.random()
                    if (attendee.id != creatorUser.id) {
                        randomAttendees.add(attendee)
                    }
                }
                val event = Event(
                    title = "Community Event $i",
                    description = "Testing authorization and data isolation mechanics with a random crowd.",
                    locationName = listOf("Casablanca, Morocco", "Online", "Paris, France", "Remote Hub").random(),
                    coordinates = GeometryUtils.createPoint(
                        latitude = asin(Random.nextDouble(-1.0, 1.0)) * (180.0 / PI),
                        longitude = Random.nextDouble(-180.0, 180.0)
                    ),
                    date = LocalDateTime.of(
                        2026,
                        Random.nextInt(1, 12),
                        Random.nextInt(1, 29),
                        Random.nextInt(0, 23),
                        0,
                        0
                    ).toInstant(ZoneOffset.UTC),
                    isPrivate = Random.nextBoolean(),
                    durationMinutes = listOf(30L, 60L, 90L, 120L, 180L).random(),
                    isPaid = Random.nextBoolean(),
                    image = null,
                    creator = creatorUser
                )
                Pair(event, randomAttendees)
            }
            eventRepository.saveAllAndFlush(batchEvents.map { it.first })
            val userEventsToSave = batchEvents.flatMap { (event, attendees) ->
                attendees.map { attendee ->
                    UserEvent(user = attendee, event = event)
                }
            }
            userEventRepository.saveAllAndFlush(userEventsToSave)
        }
    }
}