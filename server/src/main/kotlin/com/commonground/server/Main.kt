package com.commonground.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.httpMethod
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun main() {
    start()
}

fun start() {
    embeddedServer(factory = CIO, port = 8080) {
        install(CallLogging) {
            level = Level.DEBUG
            this.format { call ->
                val status = call.response.status()
                val httpMethod = call.request.httpMethod.value
                val userAgent = call.request.headers["User-Agent"]
                "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
            }
        }
        install(ContentNegotiation) {
            json(Json)
        }
        configureRouting()
    }.start(wait = true)
}

private fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("hello from server")
        }
    }
}