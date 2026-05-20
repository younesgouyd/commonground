package com.commonground.server.data.entities

import jakarta.persistence.*
import java.util.*

@Entity
class RefreshToken(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true)
    val token: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: User
)