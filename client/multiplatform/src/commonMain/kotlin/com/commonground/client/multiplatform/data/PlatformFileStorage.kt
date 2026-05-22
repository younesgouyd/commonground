package com.commonground.client.multiplatform.data

abstract class PlatformFileStorage {
    abstract suspend fun writeText(text: String)
    abstract suspend fun readText(): String?
    abstract suspend fun clear()
}