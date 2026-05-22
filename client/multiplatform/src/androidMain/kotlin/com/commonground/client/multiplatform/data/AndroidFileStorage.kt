package com.commonground.client.multiplatform.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidFileStorage(private val context: Context) : PlatformFileStorage() {
    private val file = File(context.filesDir, "tokens.json")

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
            if (file.exists()) {
                file.delete()
            }
        }
    }
}