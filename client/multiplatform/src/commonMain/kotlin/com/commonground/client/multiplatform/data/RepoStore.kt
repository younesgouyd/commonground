package com.commonground.client.multiplatform.data

import com.commonground.client.multiplatform.data.repositories.AuthRepo
import com.commonground.client.multiplatform.data.repositories.CategoryRepo
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.client.multiplatform.data.repositories.UserRepo
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.sse.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RepoStore(
    platformFileStorage: PlatformFileStorage
) {
    companion object {
        private const val SERVER_HOST = "localhost" // TODO
        private const val SERVER_PORT = 8080 // TODO
    }

    val authRepo = AuthRepo(platformFileStorage, SERVER_HOST, SERVER_PORT)

    private val client = HttpClient(CIO) {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) { json(Json) }
        install(SSE) // for notifications
        install(HttpTimeout) {
            this.requestTimeoutMillis = 30*60*1000 // TODO
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTP
                host = SERVER_HOST
                port = SERVER_PORT
                path("api/v1/")
            }
        }
        install(Auth) {
            bearer {
                loadTokens {
                    authRepo.loadTokens()?.let {
                        BearerTokens(it.accessToken, it.refreshToken)
                    }
                }
                refreshTokens {
                    authRepo.refreshToken()?.let {
                        BearerTokens(it.accessToken, it.refreshToken)
                    }
                }
                sendWithoutRequest { request -> request.url.host == SERVER_HOST }
            }
        }
    }

    val eventRepo = EventRepo(client)
    val userRepo = UserRepo(client)
    lateinit var categoryRepo : CategoryRepo
}
