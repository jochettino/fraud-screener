package com.jochettino.fraudscreener.api.application.port.`in`

import com.jochettino.fraudscreener.api.domain.OutboundBankTransfer
import com.jochettino.fraudscreener.api.domain.ScreeningStatus

interface ScreenOutboundBankTransferUseCase {
    /** @throws com.jochettino.fraudscreener.api.domain.InstrumentNotFoundException if an instrument is unknown */
    fun screen(transfer: OutboundBankTransfer): ScreeningStatus
}
