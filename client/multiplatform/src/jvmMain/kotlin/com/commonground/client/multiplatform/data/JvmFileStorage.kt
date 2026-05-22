package com.commonground.client.multiplatform.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class JvmFileStorage : PlatformFileStorage() {
    private val file = File(System.getProperty("user.home"), "commonground/tokens.json")

    init { file.parentFile?.mkdirs() }

    override suspend fun writeText(text: String) {
        withContext(Dispatchers.IO) {
            file.writeText(text)
        }
    }
    override suspend fun readText(): String? {
        return withContext(Dispatchers.IO) {
            if (file.exists()) file.readText() else null
        }
    }
    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            if (file.exists()) file.delete()
        }
    }
}