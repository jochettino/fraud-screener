package com.jochettino.fraudscreener.api.application.port.out

import com.jochettino.fraudscreener.api.domain.BankTransfer
import com.jochettino.fraudscreener.api.domain.InstrumentId
import com.jochettino.fraudscreener.api.domain.ScreeningStatus

interface InstrumentLookupPort {
    fun exists(id: InstrumentId): Boolean
}

fun interface FraudScreeningPort {
    fun screen(transfer: BankTransfer): ScreeningStatus
}
