package com.commonground.server.data

import com.commonground.core.models.ImageUrl
import jakarta.persistence.*
import java.util.*

@Entity
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val title: String,
    val description: String? = null,
    val location: String,
    val date: String,
    val isPrivate: Boolean,
    val durationMinutes: Long,
    val isPaid: Boolean,
    val image: ImageUrl?,

    // The back-reference for ownership
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    val creator: User,

    // The back-reference for attendance
    @ManyToMany(mappedBy = "attendingEvents")
    val attendees: MutableList<User> = mutableListOf()
)

@Entity
@Table(name = "`user`") // because user is a reserved keyword in PostgreSQL
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val username: String,
    val password: String,
    val displayName: String? = null,
    val bio: String? = null,
    val emailAddress: String? = null,
    val profilePic: ImageUrl? = null,

    @OneToMany(mappedBy = "creator", cascade = [CascadeType.ALL])
    val createdEvents: MutableList<Event> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "event_attendees",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "event_id")]
    )
    val attendingEvents: MutableList<Event> = mutableListOf()
)