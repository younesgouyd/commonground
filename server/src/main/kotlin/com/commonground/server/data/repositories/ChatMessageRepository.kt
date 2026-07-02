package com.commonground.server.data.repositories

import com.commonground.server.data.entities.ChatMessageEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ChatMessageRepository : JpaRepository<ChatMessageEntity, UUID> {

    @Query("""
        SELECT m FROM ChatMessageEntity m
        JOIN FETCH m.sender
        WHERE m.event.id = :eventId
        ORDER BY m.createdAt ASC, m.id ASC
    """)
    fun findByEventIdOrderByCreatedAtAsc(
        eventId: UUID,
        pageable: Pageable
    ): Slice<ChatMessageEntity>
}
