package com.jochettino.fraudscreener.api.application.service

import com.jochettino.fraudscreener.api.application.port.`in`.ScreenInboundBankTransferUseCase
import com.jochettino.fraudscreener.api.application.port.out.FraudScreeningPort
import com.jochettino.fraudscreener.api.application.port.out.InstrumentLookupPort
import com.jochettino.fraudscreener.api.domain.InboundBankTransfer
import com.jochettino.fraudscreener.api.domain.InstrumentNotFoundException
import com.jochettino.fraudscreener.api.domain.ScreeningStatus

class ScreenInboundBankTransferService(
    private val instruments: InstrumentLookupPort,
    private val screening: FraudScreeningPort,
) : ScreenInboundBankTransferUseCase {
    override fun screen(transfer: InboundBankTransfer): ScreeningStatus {
        listOf(transfer.from, transfer.to).forEach { if (!instruments.exists(it)) throw InstrumentNotFoundException(it) }
        return screening.screen(transfer)
    }
}
