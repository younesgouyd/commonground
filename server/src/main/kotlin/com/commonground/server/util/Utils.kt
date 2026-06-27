package com.commonground.server.util

import java.util.*

fun String.toUuid(): UUID = UUID.fromString(this)!!