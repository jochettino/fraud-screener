package com.jochettino.fraudscreener.api.adapter.out

import com.jochettino.fraudscreener.api.application.port.out.FraudScreeningPort
import com.jochettino.fraudscreener.api.application.port.out.InstrumentLookupPort
import com.jochettino.fraudscreener.api.domain.InstrumentId
import com.jochettino.fraudscreener.api.domain.BankTransfer
import com.jochettino.fraudscreener.api.domain.ScreeningStatus
import java.math.BigDecimal

/** Mock: every instrument exists except ids starting with `00000000-`. */
class MockInstrumentLookup : InstrumentLookupPort {
    override fun exists(id: InstrumentId) = !id.value.toString().startsWith("00000000-")
}

/** Mock: amount > 10000 is HELD; reference containing "fraud" (case-insensitive) is REJECTED; else PASSED. */
class MockFraudScreening : FraudScreeningPort {
    override fun screen(transfer: BankTransfer) = when {
        transfer.money.amount > BigDecimal(10000) -> ScreeningStatus.HELD
        transfer.reference?.contains("fraud", ignoreCase = true) == true -> ScreeningStatus.REJECTED
        else -> ScreeningStatus.PASSED
    }
}
