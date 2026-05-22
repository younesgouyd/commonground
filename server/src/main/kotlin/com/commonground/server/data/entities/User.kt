package com.commonground.server.data.entities

import com.commonground.core.models.ImageUrl
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "`user`") // because user is a reserved keyword in PostgreSQL
class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    val username: String,

    val password: String,

    @Column(unique = true)
    val emailAddress: String? = null,

    val displayName: String? = null,
    val bio: String? = null,
    val profilePic: ImageUrl? = null,

    @OneToMany(mappedBy = "creator", cascade = [CascadeType.ALL])
    val createdEvents: MutableList<Event> = mutableListOf(),

    @ManyToMany
    val attendingEvents: MutableList<Event> = mutableListOf()
)