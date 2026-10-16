package com.jochettino.fraudscreener.api.adapter.`in`.http

import com.jochettino.fraudscreener.api.adapter.http.dto.InboundBankTransferRequest
import com.jochettino.fraudscreener.api.adapter.http.dto.ScreeningResponse
import com.jochettino.fraudscreener.api.adapter.http.dto.ScreeningStatus as ScreeningStatusDto
import com.jochettino.fraudscreener.api.application.port.`in`.ScreenInboundBankTransferUseCase
import com.jochettino.fraudscreener.api.domain.InboundBankTransfer
import com.jochettino.fraudscreener.api.domain.InstrumentId
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.inboundBankTransferRoutes(useCase: ScreenInboundBankTransferUseCase) {
    post("/v1/banktransfer/inbound") {
        val transfer = call.receive<InboundBankTransferRequest>().toDomain()
        val status = ScreeningStatusDto.valueOf(useCase.screen(transfer).name)
        call.respond(ScreeningResponse(status))
    }
}

private fun InboundBankTransferRequest.toDomain() = InboundBankTransfer(
    InstrumentId(uuid("fromInstrumentId", fromInstrumentId)),
    InstrumentId(uuid("toInstrumentId", toInstrumentId)),
    validMoney(money.currency, money.amount, reference),
    reference,
)
