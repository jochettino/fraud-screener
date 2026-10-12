package com.jochettino.fraudscreener.api.adapter.`in`.http

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthRoutes() {
    get("/health") {
        call.respondText("""{"status":"UP"}""", ContentType.Application.Json)
    }
}
