package com.jochettino.fraudscreener.api.application.port.`in`

import com.jochettino.fraudscreener.api.domain.InboundBankTransfer
import com.jochettino.fraudscreener.api.domain.ScreeningStatus

interface ScreenInboundBankTransferUseCase {
    /** @throws com.jochettino.fraudscreener.api.domain.InstrumentNotFoundException if an instrument is unknown */
    fun screen(transfer: InboundBankTransfer): ScreeningStatus
}
