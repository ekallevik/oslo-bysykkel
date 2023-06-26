package com.example

import com.example.domain.StationOverviewParams
import com.example.domain.StationsOverview
import com.example.services.GbfsService
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(ContentNegotiation) {
        json()
    }

    configureRouting()
}

fun Application.configureRouting() {

    val baseUrl = "https://gbfs.urbansharing.com/oslobysykkel.no"

    val client = HttpClient() {
        install(HttpCache)
        install(ClientContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            header("Client-Identifier", "ekallevik-bysykkel")
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/stations") {

            val parameters = StationOverviewParams.parse(
                call.request.queryParameters["excludeMissingStatus"]?.toBoolean(),
                call.request.queryParameters["excludeNonInstalled"]?.toBoolean(),
                call.request.queryParameters["excludeEmpty"]?.toBoolean(),
                call.request.queryParameters["excludeFull"]?.toBoolean(),
            )

            val stations = GbfsService.getBicycleStations(client, baseUrl)
            val response = StationsOverview.valueOf(stations, parameters)

            call.respond(response)
        }
    }
}
