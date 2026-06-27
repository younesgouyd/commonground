package com.commonground.server.data.entities

import com.commonground.core.models.ImageUrl
import jakarta.persistence.*
import org.locationtech.jts.geom.Point
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class Event(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val title: String,

    val description: String? = null,

    @Column(nullable = false)
    val locationName: String,

    @Column(columnDefinition = "geography(Point, 4326)")
    val coordinates: Point,

    @Column(nullable = false)
    val startDate: Instant,

    val endDate: Instant?,

    @Column(nullable = false) val isPrivate: Boolean, // followers only
    @Column(nullable = false) val isPrivatePlace: Boolean, // home, hotel, restaurant, school... etc
    @Column(nullable = false) val isPaid: Boolean,

    val image: ImageUrl?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    val creator: User
) {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    lateinit var createdAt: Instant

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: Instant
}