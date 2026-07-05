package com.commonground.client.multiplatform.data.repositories

import com.commonground.client.multiplatform.data.PlatformFileStorage
import com.commonground.core.models.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

class AuthRepo(
    private val storage: PlatformFileStorage,
    serverHost: String,
    serverPort: Int
) {
    val logger = KotlinLogging.logger {  }

    private val client = HttpClient(CIO) {
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
    suspend fun loadFromDisk() {
        val json = storage.readText() ?: return
        val tokens = try {
            Json.decodeFromString<TokenPair>(json)
        } catch (_: Exception) { null }
        if (tokens != null) {
            mutex.withLock { cachedTokens = tokens }
        }
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

    suspend fun clearTokens() {
        mutex.withLock {
            cachedTokens = null
            storage.clear()
        }
    }

    private suspend fun setTokens(tokens: TokenPair) {
        mutex.withLock {
            cachedTokens = tokens
            val json = Json.encodeToString(tokens)
            storage.writeText(json)
        }
    }
}
