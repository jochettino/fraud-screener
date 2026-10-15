package com.jochettino.fraudscreener.api.application.service

import com.jochettino.fraudscreener.api.application.port.`in`.ScreenOutboundBankTransferUseCase
import com.jochettino.fraudscreener.api.application.port.out.FraudScreeningPort
import com.jochettino.fraudscreener.api.application.port.out.InstrumentLookupPort
import com.jochettino.fraudscreener.api.domain.InstrumentNotFoundException
import com.jochettino.fraudscreener.api.domain.OutboundBankTransfer
import com.jochettino.fraudscreener.api.domain.ScreeningStatus

class ScreenOutboundBankTransferService(
    private val instruments: InstrumentLookupPort,
    private val screening: FraudScreeningPort,
) : ScreenOutboundBankTransferUseCase {
    override fun screen(transfer: OutboundBankTransfer): ScreeningStatus {
        listOf(transfer.from, transfer.to).forEach { if (!instruments.exists(it)) throw InstrumentNotFoundException(it) }
        return screening.screen(transfer)
    }
}
