package com.jochettino.fraudscreener.api

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

private const val DEFAULT_PORT = 8080

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
    embeddedServer(Netty, port = port) {
        // No routes yet: the skeleton only boots the server.
    }.start(wait = true)
}
