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

    suspend fun signUp(email: String, username: String, password: String): SignUpResult {
        val result = client.post("auth/signup") {
            setBody(SignUpRequest(email.trim(), username.trim(), password))
        }.body<SignUpResult>()
        if (result is SignUpResult.Success) {
            saveTokens(result.tokens)
        }
        return result
    }

    suspend fun login(login: String, password: String): LoginResult {
        val result = client.post("auth/login") {
            setBody(LoginRequest(login, password))
        }.body<LoginResult>()
        if (result is LoginResult.Success) {
            saveTokens(result.tokens)
        }
        return result
    }

    suspend fun refreshToken(): TokenPair? {
        val refresh = loadTokens()?.refreshToken // TODO: check null
        val token = client.post("auth/refresh") {
            setBody(refresh)
        }.body<TokenPair?>()
        if (token != null) {
            clearTokens()
            saveTokens(token)
        }
        return token
    }

    suspend fun logout() {
        // TODO: call auth/logout
        clearTokens()
    }

    suspend fun loadTokens(): TokenPair? {
        val json = storage.readText() ?: return null
        return try {
            Json.decodeFromString<TokenPair>(json)
        } catch (e: Exception) {
            logger.error(e) { }
            null
        }
    }

    suspend fun clearTokens() {
        storage.clear()
    }

    private suspend fun saveTokens(tokens: TokenPair) {
        val json = Json.encodeToString(tokens)
        storage.writeText(json)
    }
}
