package com.jochettino.fraudscreener.api.application.port.out

import com.jochettino.fraudscreener.api.domain.InstrumentId
import com.jochettino.fraudscreener.api.domain.OutboundBankTransfer
import com.jochettino.fraudscreener.api.domain.ScreeningStatus

interface InstrumentLookupPort {
    fun exists(id: InstrumentId): Boolean
}

fun interface FraudScreeningPort {
    fun screen(transfer: OutboundBankTransfer): ScreeningStatus
}
