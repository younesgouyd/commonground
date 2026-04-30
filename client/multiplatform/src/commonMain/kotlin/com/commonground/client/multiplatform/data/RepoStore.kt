package com.commonground.client.multiplatform.data

import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.client.multiplatform.data.repositories.UserRepo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
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
}