package com.commonground.server.data.entities

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*

@Entity
class RefreshToken(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true)
    val token: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val user: User
)