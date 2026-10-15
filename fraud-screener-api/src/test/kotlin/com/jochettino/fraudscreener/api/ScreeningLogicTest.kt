package com.jochettino.fraudscreener.api

import com.jochettino.fraudscreener.api.adapter.out.MockFraudScreening
import com.jochettino.fraudscreener.api.adapter.out.MockInstrumentLookup
import com.jochettino.fraudscreener.api.application.port.out.FraudScreeningPort
import com.jochettino.fraudscreener.api.application.service.ScreenOutboundBankTransferService
import com.jochettino.fraudscreener.api.domain.InstrumentId
import com.jochettino.fraudscreener.api.domain.InstrumentNotFoundException
import com.jochettino.fraudscreener.api.domain.Money
import com.jochettino.fraudscreener.api.domain.OutboundBankTransfer
import com.jochettino.fraudscreener.api.domain.ScreeningStatus
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ScreeningLogicTest {
    private val known = InstrumentId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
    private val missing = InstrumentId(UUID.fromString("00000000-0000-0000-0000-000000000001"))

    private fun transfer(amount: String = "1", reference: String? = null, from: InstrumentId = known, to: InstrumentId = known) =
        OutboundBankTransfer(from, to, Money("EUR", BigDecimal(amount)), reference)

    @Test
    fun `mock screening rules`() {
        val screening = MockFraudScreening()
        assertEquals(ScreeningStatus.PASSED, screening.screen(transfer("10000")))
        assertEquals(ScreeningStatus.HELD, screening.screen(transfer("10000.01")))
        assertEquals(ScreeningStatus.REJECTED, screening.screen(transfer(reference = "my FRAUD ref")))
        assertEquals(ScreeningStatus.HELD, screening.screen(transfer("20000", "fraud")))
    }

    @Test
    fun `service throws for unknown instrument and skips screening`() {
        val never = FraudScreeningPort { error("must not be called") }
        val service = ScreenOutboundBankTransferService(MockInstrumentLookup(), never)
        assertFailsWith<InstrumentNotFoundException> { service.screen(transfer(from = missing)) }
        assertFailsWith<InstrumentNotFoundException> { service.screen(transfer(to = missing)) }
    }

    @Test
    fun `service delegates to screening for known instruments`() {
        val service = ScreenOutboundBankTransferService(MockInstrumentLookup()) { ScreeningStatus.HELD }
        assertEquals(ScreeningStatus.HELD, service.screen(transfer()))
    }
}
