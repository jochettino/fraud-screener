package com.jochettino.fraudscreener.api

import com.jochettino.fraudscreener.api.adapter.`in`.http.healthRoutes
import com.jochettino.fraudscreener.api.adapter.`in`.http.installErrorHandling
import com.jochettino.fraudscreener.api.adapter.`in`.http.inboundBankTransferRoutes
import com.jochettino.fraudscreener.api.adapter.`in`.http.outboundBankTransferRoutes
import com.jochettino.fraudscreener.api.config.screenInboundBankTransferUseCase
import com.jochettino.fraudscreener.api.config.screenOutboundBankTransferUseCase
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

private const val DEFAULT_PORT = 8080

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) { json() }
    installErrorHandling()
    routing {
        healthRoutes()
        swaggerUI(path = "swagger", swaggerFile = "openapi/fraud-screener-api.yaml")
        outboundBankTransferRoutes(screenOutboundBankTransferUseCase())
        inboundBankTransferRoutes(screenInboundBankTransferUseCase())
    }
}
