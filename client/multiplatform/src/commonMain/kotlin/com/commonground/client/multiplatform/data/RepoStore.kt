package com.commonground.client.multiplatform.data

import com.commonground.client.multiplatform.data.repositories.AuthRepo
import com.commonground.client.multiplatform.data.repositories.CategoryRepo
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.client.multiplatform.data.repositories.UserRepo
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.sse.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RepoStore {
    private val client = HttpClient(CIO) {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) { json(Json) }
        install(SSE) // for notifications
        install(HttpTimeout) {
            this.requestTimeoutMillis = 30*60*1000 // TODO
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.commonground.com" // TODO
                path("v1/")
            }
        }
    }

    val eventRepo = EventRepo(client)
    val userRepo = UserRepo(client)
    lateinit var authRepo : AuthRepo
    lateinit var categoryRepo : CategoryRepo
}