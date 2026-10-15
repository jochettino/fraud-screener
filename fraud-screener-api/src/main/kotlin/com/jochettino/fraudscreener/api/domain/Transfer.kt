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

sealed interface BankTransfer {
    val from: InstrumentId
    val to: InstrumentId
    val money: Money
    val reference: String?
}

data class OutboundBankTransfer(
    override val from: InstrumentId,
    override val to: InstrumentId,
    override val money: Money,
    override val reference: String?,
) : BankTransfer

data class InboundBankTransfer(
    override val from: InstrumentId,
    override val to: InstrumentId,
    override val money: Money,
    override val reference: String?,
) : BankTransfer

enum class ScreeningStatus { PASSED, REJECTED, HELD }

class InstrumentNotFoundException(val id: InstrumentId) : RuntimeException("Instrument ${id.value} not found")
