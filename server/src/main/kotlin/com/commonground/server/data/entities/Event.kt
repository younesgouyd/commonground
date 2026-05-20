package com.commonground.server.data.entities

import com.commonground.core.models.ImageUrl
import jakarta.persistence.*
import java.util.*

@Entity
class Event(
    @Id
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String? = null,
    val location: String,
    val date: String,
    val isPrivate: Boolean,
    val durationMinutes: Long,
    val isPaid: Boolean,
    val image: ImageUrl?,

    @ManyToOne(fetch = FetchType.LAZY)
    val creator: User,

    @ManyToMany(mappedBy = "attendingEvents")
    val attendees: MutableList<User> = mutableListOf()
)