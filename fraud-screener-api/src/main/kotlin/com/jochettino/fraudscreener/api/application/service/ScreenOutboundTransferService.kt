package com.jochettino.fraudscreener.api.application.service

import com.jochettino.fraudscreener.api.application.port.`in`.ScreenOutboundTransferUseCase
import com.jochettino.fraudscreener.api.application.port.out.FraudScreeningPort
import com.jochettino.fraudscreener.api.application.port.out.InstrumentLookupPort
import com.jochettino.fraudscreener.api.domain.InstrumentNotFoundException
import com.jochettino.fraudscreener.api.domain.OutboundTransfer
import com.jochettino.fraudscreener.api.domain.ScreeningStatus

class ScreenOutboundTransferService(
    private val instruments: InstrumentLookupPort,
    private val screening: FraudScreeningPort,
) : ScreenOutboundTransferUseCase {
    override fun screen(transfer: OutboundTransfer): ScreeningStatus {
        listOf(transfer.from, transfer.to).forEach { if (!instruments.exists(it)) throw InstrumentNotFoundException(it) }
        return screening.screen(transfer)
    }
}
