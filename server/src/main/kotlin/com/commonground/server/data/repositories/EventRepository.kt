package com.commonground.server.data.repositories

import com.commonground.core.models.ImageUrl
import com.commonground.server.data.entities.Event
import com.commonground.server.data.entities.User
import org.locationtech.jts.geom.Point
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

interface EventRepository : JpaRepository<Event, UUID> {
    @Modifying
    @Transactional
    @Query("""
        UPDATE Event
        SET title = :title,
            description = :description,
            locationName = :locationName,
            coordinates = :coordinates,
            startDate = :startDate,
            endDate = :endDate,
            isPrivate = :isPrivate,
            isPrivatePlace = :isPrivatePlace,
            isPaid = :isPaid,
            updatedAt = CURRENT_TIMESTAMP
        WHERE id = :id
    """)
    fun update(
        id: UUID,
        title: String,
        description: String?,
        locationName: String,
        coordinates: Point?,
        startDate: Instant,
        endDate: Instant?,
        isPrivate: Boolean,
        isPrivatePlace: Boolean,
        isPaid: Boolean
    )

    @Modifying
    @Transactional
    @Query("""
            UPDATE Event
            SET image = :image,
                updatedAt = CURRENT_TIMESTAMP
            WHERE id = :id
    """)
    fun updateImage(
        id: UUID,
        image: ImageUrl?
    )

    // TODO: add date condition
    @Query(
        value = """
            SELECT *
            FROM event e
            WHERE ST_DWithin(e.coordinates, CAST(:location AS geography), :radiusMeters, true)
            AND (
                e.is_private IS NOT TRUE
                OR CAST(:observerUserId AS uuid) = e.creator_id
                OR e.creator_id IN (SELECT uf.followee_user_id FROM user_follow uf WHERE uf.follower_user_id = CAST(:observerUserId AS uuid))
            )
            ORDER BY e.coordinates <-> CAST(:location AS geography) ASC, e.id DESC
        """,
        nativeQuery = true
    )
    fun findEventsNearLocation(
        location: Point,
        radiusMeters: Int,
        observerUserId: UUID,
        pageable: Pageable
    ): Slice<Event>

    @Query("""
        SELECT e
        FROM Event e
        WHERE e.creator = :creator
        AND (
            e.isPrivate IS NOT TRUE
            OR :observer = :creator
            OR e.creator IN (SELECT uf.followee FROM UserFollow uf WHERE uf.follower = :observer)
        )
    """)
    fun findByCreator(
        creator: User,
        observer: User,
        pageable: Pageable
    ): Page<Event>

    @Query(
        value = """
            SELECT ue.event
            FROM UserEvent ue
            WHERE ue.user = :user
            AND ue.event.startDate > :date
            AND (
                ue.event.isPrivate IS NOT TRUE
                OR ue.event.creator IN (SELECT uf.followee FROM UserFollow uf WHERE uf.follower = :observer)
            )
            ORDER BY ue.event.startDate ASC, ue.event.id ASC
        """
    )
    fun findUserEventsAfterDate(
        user: User,
        date: Instant,
        observer: User,
        pageable: Pageable
    ): Page<Event>


    @Query(
        value = """
            SELECT ue.event
            FROM UserEvent ue
            WHERE ue.user = :user
            AND ue.event.startDate < :date
            AND (
                ue.event.isPrivate IS NOT TRUE
                OR ue.event.creator IN (SELECT uf.followee FROM UserFollow uf WHERE uf.follower = :observer)
            )
            ORDER BY ue.event.startDate DESC, ue.event.id ASC
        """
    )
    fun findUserEventsBeforeDate(
        user: User,
        date: Instant,
        observer: User,
        pageable: Pageable
    ): Page<Event>
}