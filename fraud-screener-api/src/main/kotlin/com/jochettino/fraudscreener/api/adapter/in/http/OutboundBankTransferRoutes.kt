package com.jochettino.fraudscreener.api.adapter.`in`.http

import com.jochettino.fraudscreener.api.adapter.http.dto.OutboundBankTransferRequest
import com.jochettino.fraudscreener.api.adapter.http.dto.ScreeningResponse
import com.jochettino.fraudscreener.api.adapter.http.dto.ScreeningStatus as ScreeningStatusDto
import com.jochettino.fraudscreener.api.application.port.`in`.ScreenOutboundBankTransferUseCase
import com.jochettino.fraudscreener.api.domain.InstrumentId
import com.jochettino.fraudscreener.api.domain.OutboundBankTransfer
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.outboundBankTransferRoutes(useCase: ScreenOutboundBankTransferUseCase) {
    post("/v1/banktransfer/outbound") {
        val transfer = call.receive<OutboundBankTransferRequest>().toDomain()
        val status = ScreeningStatusDto.valueOf(useCase.screen(transfer).name)
        call.respond(ScreeningResponse(status))
    }
}

private fun OutboundBankTransferRequest.toDomain() = OutboundBankTransfer(
    InstrumentId(uuid("fromInstrumentId", fromInstrumentId)),
    InstrumentId(uuid("toInstrumentId", toInstrumentId)),
    validMoney(money.currency, money.amount),
    validReference(reference),
)
