package com.commonground.server.data.entities

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "event_id"])]
)
class UserEvent(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "event_id", nullable = false)
    val event: Event
) {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    lateinit var createdAt: Instant

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: Instant
}