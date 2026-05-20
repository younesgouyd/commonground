package com.commonground.server

import com.commonground.core.models.Event
import com.commonground.core.models.User

fun com.commonground.server.data.entities.Event.toModel() = Event(
    id = id.toString(),
    title = title,
    description = description,
    location = location,
    date = date,
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
