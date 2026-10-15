package com.jochettino.fraudscreener.api.adapter.`in`.http

import com.jochettino.fraudscreener.api.adapter.http.dto.Error
import com.jochettino.fraudscreener.api.domain.InstrumentNotFoundException
import com.jochettino.fraudscreener.api.domain.Money
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.SerializationException
import java.math.BigDecimal
import java.util.UUID

class InvalidRequestException(message: String) : RuntimeException(message)

private const val MAX_REFERENCE = 20
private val CURRENCY = Regex("^[A-Z]{3}$")
private val AMOUNT = Regex("^\\d+(\\.\\d+)?$")
private val UUID_FORMAT = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

fun Application.installErrorHandling() {
    install(StatusPages) {
        exception<InstrumentNotFoundException> { call, e -> call.respond(HttpStatusCode.NotFound, Error(e.message ?: "Not found")) }
        exception<InvalidRequestException> { call, e -> call.respond(HttpStatusCode.BadRequest, Error(e.message ?: "Bad request")) }
        // Malformed JSON and missing required fields arrive as these.
        exception<SerializationException> { call, _ -> call.respond(HttpStatusCode.BadRequest, Error("Malformed request body")) }
        exception<BadRequestException> { call, _ -> call.respond(HttpStatusCode.BadRequest, Error("Malformed request body")) }
    }
}

/** Validates the fields shared by inbound and outbound requests and builds the domain [Money]. */
fun validMoney(currency: String, amount: String, reference: String?): Money {
    if (!CURRENCY.matches(currency)) throw InvalidRequestException("money.currency must be a 3-letter code")
    if (!AMOUNT.matches(amount) || BigDecimal(amount).signum() <= 0) {
        throw InvalidRequestException("money.amount must be a positive number")
    }
    if ((reference?.length ?: 0) > MAX_REFERENCE) throw InvalidRequestException("reference must be at most $MAX_REFERENCE characters")
    return Money(currency, BigDecimal(amount))
}

fun uuid(field: String, value: String): UUID =
    if (UUID_FORMAT.matches(value)) UUID.fromString(value) else throw InvalidRequestException("$field must be a UUID")
