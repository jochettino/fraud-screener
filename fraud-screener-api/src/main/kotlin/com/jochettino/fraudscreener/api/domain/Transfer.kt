package com.jochettino.fraudscreener.api.domain

import java.math.BigDecimal
import java.util.UUID

@JvmInline
value class InstrumentId(val value: UUID)

data class Money(val currency: String, val amount: BigDecimal) {
    init {
        require(amount.signum() > 0) { "amount must be positive" }
    }
}

data class OutboundTransfer(
    val from: InstrumentId,
    val to: InstrumentId,
    val money: Money,
    val reference: String?,
)

enum class ScreeningStatus { PASSED, REJECTED, HELD }

class InstrumentNotFoundException(val id: InstrumentId) : RuntimeException("Instrument ${id.value} not found")
