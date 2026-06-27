package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
enum class UserEventType { Created, Attending, Went }