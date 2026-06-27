package com.commonground.client.multiplatform.data

import com.commonground.core.models.Coordinates
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object LocationManager {
    private val client = HttpClient(CIO) {
        install(Logging) { level = LogLevel.ALL }
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        defaultRequest {
            contentType(ContentType.Application.Json)
            url("https://ipinfo.io/json")
        }
    }

    @Serializable
    private data class IpInfoResponse(val loc: String)

    suspend fun getCurrentLocation(): Coordinates? {
        try {
            val response = client.get {}.body<IpInfoResponse>()
            val parts = response.loc.split(",")
            if (parts.size == 2) {
                val lat = parts[0].toDoubleOrNull()
                val lon = parts[1].toDoubleOrNull()
                if (lat != null && lon != null) {
                    return Coordinates(lat, lon)
                }
            }
            return null
        } catch (_: Exception) {
            return null
        }
    }
}