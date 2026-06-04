package com.commonground.server.data.entities

import com.commonground.core.models.ImageUrl
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "`user`") // because user is a reserved keyword in PostgreSQL
class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false) val password: String,

    @Column(unique = true)
    val emailAddress: String? = null,

    val displayName: String? = null,
    val bio: String? = null,
    val profilePic: ImageUrl? = null,

    @OneToMany(mappedBy = "creator", cascade = [CascadeType.ALL])
    val createdEvents: MutableList<Event> = mutableListOf(),

    @ManyToMany
    val attendingEvents: MutableList<Event> = mutableListOf(),
) {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    lateinit var createdAt: Instant

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: Instant
}