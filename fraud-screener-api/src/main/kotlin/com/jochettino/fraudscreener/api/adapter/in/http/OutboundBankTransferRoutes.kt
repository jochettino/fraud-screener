package com.jochettino.fraudscreener.api.adapter.`in`.http

import com.jochettino.fraudscreener.api.adapter.http.dto.Error
import com.jochettino.fraudscreener.api.adapter.http.dto.OutboundBankTransferRequest
import com.jochettino.fraudscreener.api.adapter.http.dto.ScreeningResponse
import com.jochettino.fraudscreener.api.adapter.http.dto.ScreeningStatus as ScreeningStatusDto
import com.jochettino.fraudscreener.api.application.port.`in`.ScreenOutboundBankTransferUseCase
import com.jochettino.fraudscreener.api.domain.InstrumentId
import com.jochettino.fraudscreener.api.domain.InstrumentNotFoundException
import com.jochettino.fraudscreener.api.domain.Money
import com.jochettino.fraudscreener.api.domain.OutboundBankTransfer
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
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

fun Route.OutboundBankTransferRoutes(useCase: ScreenOutboundBankTransferUseCase) {
    post("/v1/banktransfer/outbound") {
        val transfer = call.receive<OutboundBankTransferRequest>().toDomain()
        val status = ScreeningStatusDto.valueOf(useCase.screen(transfer).name)
        call.respond(ScreeningResponse(status))
    }
}

private fun OutboundBankTransferRequest.toDomain(): OutboundBankTransfer {
    if (!CURRENCY.matches(money.currency)) throw InvalidRequestException("money.currency must be a 3-letter code")
    if (!AMOUNT.matches(money.amount) || BigDecimal(money.amount).signum() <= 0) {
        throw InvalidRequestException("money.amount must be a positive number")
    }
    if ((reference?.length ?: 0) > MAX_REFERENCE) throw InvalidRequestException("reference must be at most $MAX_REFERENCE characters")
    return OutboundBankTransfer(
        InstrumentId(uuid("fromInstrumentId", fromInstrumentId)),
        InstrumentId(uuid("toInstrumentId", toInstrumentId)),
        Money(money.currency, BigDecimal(money.amount)),
        reference,
    )
}

private fun uuid(field: String, value: String): UUID =
    if (UUID_FORMAT.matches(value)) UUID.fromString(value) else throw InvalidRequestException("$field must be a UUID")
