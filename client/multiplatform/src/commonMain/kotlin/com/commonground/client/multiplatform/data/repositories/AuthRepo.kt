package com.commonground.client.multiplatform.data.repositories

import com.commonground.client.multiplatform.data.SettingsManager
import com.commonground.core.models.*
import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlin.concurrent.Volatile

class AuthRepo(
    private val settingsManager: SettingsManager,
    serverHost: String,
    serverPort: Int
) {
    private val client = HttpClient {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) { json(Json) }
        defaultRequest {
            contentType(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTP
                host = serverHost
                port = serverPort
                path("api/v1/")
            }
        }
    }

    // In-memory cache. On login/signup/refresh it's updated immediately.
    // Must call loadFromDisk() on startup to restore persisted tokens.
    @Volatile
    private var cachedTokens: TokenPair? = null
    private val mutex = Mutex()

    /** Call once at app startup to restore persisted tokens from disk. */
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    suspend fun loadFromDisk() {
        val tokens = settingsManager.getTokens() ?: return
        mutex.withLock { cachedTokens = tokens }
    }

    suspend fun signUp(email: String, username: String, password: String): SignUpResult {
        val result = client.post("auth/signup") {
            setBody(SignUpRequest(email.trim(), username.trim(), password))
        }.body<SignUpResult>()
        if (result is SignUpResult.Success) {
            setTokens(result.tokens)
        }
        return result
    }

    suspend fun login(login: String, password: String): LoginResult {
        val result = client.post("auth/login") {
            setBody(LoginRequest(login, password))
        }.body<LoginResult>()
        if (result is LoginResult.Success) {
            setTokens(result.tokens)
        }
        return result
    }

    suspend fun refreshToken(): TokenPair? {
        val refresh = cachedTokens?.refreshToken ?: return null
        return try {
            val token = client.post("auth/refresh") {
                setBody(refresh)
            }.body<TokenPair?>()
            if (token != null) {
                setTokens(token)
            }
            token
        } catch (_: Exception) {
            clearTokens()
            null
        }
    }

    fun loadTokens(): TokenPair? = cachedTokens

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    suspend fun clearTokens() {
        mutex.withLock {
            cachedTokens = null
            settingsManager.clearTokens()
        }
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    private suspend fun setTokens(tokens: TokenPair) {
        mutex.withLock {
            cachedTokens = tokens
            settingsManager.setTokens(tokens)
        }
    }
}
