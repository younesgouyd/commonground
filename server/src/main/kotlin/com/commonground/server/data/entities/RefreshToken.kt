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
class RefreshToken(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    val token: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(nullable = false)
    val user: User,
) {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    lateinit var createdAt: Instant

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: Instant
}