package com.commonground.server.data

import com.commonground.server.data.entities.Event
import com.commonground.server.data.entities.RefreshToken
import com.commonground.server.data.entities.User
import org.locationtech.jts.geom.Point
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface EventRepository : JpaRepository<Event, UUID> {
    @Query(
        value = """
            SELECT * FROM event e 
            WHERE ST_DWithin(e.coordinates, CAST(:location AS geography), :radiusMeters, true)
            ORDER BY e.coordinates <-> CAST(:location AS geography)
        """,
        nativeQuery = true
    )
    fun findEventsNearLocation(
        @Param("location") location: Point,
        @Param("radiusMeters") radiusMeters: Int,
        pageable: Pageable
    ): Slice<Event>
}

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsernameOrEmailAddress(username: String, emailAddress: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmailAddress(emailAddress: String): Boolean
}

interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
    fun existsByUserIdAndToken(userId: UUID, token: String): Boolean
    fun deleteByUserIdAndToken(userId: UUID, token: String)
}