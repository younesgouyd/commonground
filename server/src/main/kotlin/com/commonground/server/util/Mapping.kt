package com.commonground.server.util

import com.commonground.core.models.Event
import com.commonground.core.models.User
import kotlin.time.toKotlinInstant

fun com.commonground.server.data.entities.Event.toModel() = Event(
    id = id.toString(),
    title = title,
    description = description,
    locationName = locationName,
    coordinates = Event.Coordinates(latitude = coordinates.y, longitude =  coordinates.x),
    date = date.toKotlinInstant(),
    isPrivate = isPrivate,
    durationMinutes = durationMinutes,
    isPaid = isPaid,
    image = image,
    creator = creator.toModel()
)

fun com.commonground.server.data.entities.User.toModel() = User(
    id = id.toString(),
    username = username,
    displayName = displayName,
    bio = bio,
    emailAddress = emailAddress,
    profilePic = profilePic
)
