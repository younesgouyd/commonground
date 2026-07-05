package com.commonground.client.multiplatform.data

import com.commonground.client.multiplatform.data.repositories.AuthRepo
import com.commonground.client.multiplatform.data.repositories.CategoryRepo
import com.commonground.client.multiplatform.data.repositories.ChatRepo
import com.commonground.client.multiplatform.data.repositories.EventRepo
import com.commonground.client.multiplatform.data.repositories.UserRepo
import com.commonground.client.multiplatform.platform
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
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json

/** Mutable holder so repos always use the current HttpClient after client resets. */
class HttpClientHolder(var client: HttpClient)

class RepoStore(
    platformFileStorage: PlatformFileStorage,
    private val onRefreshTokenExpired: () -> Unit
) {
    companion object {
        const val SERVER_PORT = 8080 // TODO
        val serverHost = when (platform) {
            com.commonground.client.multiplatform.Platform.ANDROID -> "192.168.100.109"
            com.commonground.client.multiplatform.Platform.JVM -> "localhost"
        }
    }

    val authRepo = AuthRepo(platformFileStorage, serverHost, SERVER_PORT)

    private val holder = HttpClientHolder(buildClient())

    val eventRepo = EventRepo(holder)
    val userRepo = UserRepo(holder)
    val categoryRepo = CategoryRepo(holder)
    val chatRepo = ChatRepo(serverHost, SERVER_PORT)

    /**
     * Recreates the shared HttpClient. Called after login/logout so the Ktor Auth
     * plugin starts with a fresh token cache (the plugin caches currentToken internally
     * and never calls loadTokens again after the first load).
     */
    fun resetClient() {
        holder.client.close()
        holder.client = buildClient()
    }

    private fun buildClient(): HttpClient = HttpClient(CIO) {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) { json(Json) }
        install(SSE)
        install(HttpTimeout) {
            this.requestTimeoutMillis = 30 * 60 * 1000
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTP
                host = serverHost
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
                    } ?: run {
                        onRefreshTokenExpired()
                        null
                    }
                }
            }
        }
    }
}
