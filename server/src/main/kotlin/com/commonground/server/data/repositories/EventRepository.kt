package com.commonground.server.data.repositories

import com.commonground.server.data.entities.Event
import com.commonground.server.data.entities.User
import org.locationtech.jts.geom.Point
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.*

interface EventRepository : JpaRepository<Event, UUID> {
    @Query(
        value = """
            SELECT * FROM event e 
            WHERE ST_DWithin(e.coordinates, CAST(:location AS geography), :radiusMeters, true)
            ORDER BY e.coordinates <-> CAST(:location AS geography) ASC, e.id DESC
        """,
        nativeQuery = true
    )
    fun findEventsNearLocation(
        @Param("location") location: Point,
        @Param("radiusMeters") radiusMeters: Int,
        pageable: Pageable
    ): Slice<Event>

    fun findByCreator(creator: User, pageable: Pageable): Page<Event>

    @Query(
        value = """
            SELECT ue.event
            FROM UserEvent ue
            WHERE ue.user = :user
            AND ue.event.date > :date
            ORDER BY ue.event.date ASC, ue.event.id ASC
        """
    )
    fun findUserEventsAfterDate(
        @Param("user") user: User,
        @Param("date") date: Instant,
        pageable: Pageable
    ): Page<Event>


    @Query(
        value = """
            SELECT ue.event
            FROM UserEvent ue
            WHERE ue.user = :user
            AND ue.event.date < :date
            ORDER BY ue.event.date DESC, ue.event.id ASC
        """
    )
    fun findUserEventsBeforeDate(
        @Param("user") user: User,
        @Param("date") date: Instant,
        pageable: Pageable
    ): Page<Event>
}