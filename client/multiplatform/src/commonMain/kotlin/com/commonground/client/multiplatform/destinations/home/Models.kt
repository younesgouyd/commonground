package com.commonground.client.multiplatform.destinations.home

data class Event(
    val id: Long,
    val title: String,
    val description: String,
    val location: String,
    val date: String,
    val isPrivate: Boolean,
    val duration: String,
    val isPaid: Boolean
)