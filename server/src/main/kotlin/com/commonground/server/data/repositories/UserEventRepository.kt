package com.commonground.server.data.repositories

import com.commonground.core.models.User
import com.commonground.server.data.entities.UserEvent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserEventRepository : JpaRepository<UserEvent, UUID> {

    @Query("SELECT ue FROM UserEvent ue WHERE ue.event.id = :eventId AND ue.user.id = :userId")
    fun findByEventIdAndUserId(eventId: UUID, userId: UUID): UserEvent?

    @Modifying
    @Transactional
    @Query("DELETE FROM UserEvent ue WHERE ue.event.id = :eventId AND ue.user.id = :userId")
    fun deleteByEventIdAndUserId(eventId: UUID, userId: UUID)

    @Query("""
        SELECT
            CAST(ue.user.id AS string), ue.user.username, ue.user.displayName, ue.user.bio, ue.user.emailAddress, ue.user.profilePic,
            CASE WHEN (SELECT 1 FROM UserFollow uf WHERE uf.follower.id = :observerUserId AND uf.followee = ue.user) IS NOT NULL THEN true ELSE false END AS isFollowed
        FROM UserEvent ue
        WHERE ue.event.id = :eventId
        ORDER BY ue.createdAt ASC, ue.id ASC
    """)
    fun findAttendeesWithFollowState(
        @Param("eventId") eventId: UUID,
        @Param("observerUserId") observerUserId: UUID,
        pageable: Pageable
    ): Page<User>
}