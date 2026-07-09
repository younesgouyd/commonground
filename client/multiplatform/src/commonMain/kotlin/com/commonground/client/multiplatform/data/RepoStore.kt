package com.commonground.client.multiplatform.data

import com.commonground.client.multiplatform.Platform
import com.commonground.client.multiplatform.data.repositories.*
import com.commonground.client.multiplatform.platform
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.sse.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/** Mutable holder so repos always use the current HttpClient after client resets. */
class HttpClientHolder(var client: HttpClient)

class RepoStore(
    private val onRefreshTokenExpired: () -> Unit
) {
    companion object {
        const val SERVER_PORT = 8080 // TODO
        val serverHost = when (platform) {
            Platform.ANDROID -> "10.0.2.2"
            else -> "localhost"
        }
    }

    private val settingsManager = SettingsManager()

    val authRepo = AuthRepo(settingsManager, serverHost, SERVER_PORT)

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

    private fun buildClient(): HttpClient = HttpClient {
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
