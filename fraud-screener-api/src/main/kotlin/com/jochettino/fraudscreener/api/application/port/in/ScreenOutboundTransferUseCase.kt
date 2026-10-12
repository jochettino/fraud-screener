package com.jochettino.fraudscreener.api.application.port.`in`

import com.jochettino.fraudscreener.api.domain.OutboundTransfer
import com.jochettino.fraudscreener.api.domain.ScreeningStatus

interface ScreenOutboundTransferUseCase {
    /** @throws com.jochettino.fraudscreener.api.domain.InstrumentNotFoundException if an instrument is unknown */
    fun screen(transfer: OutboundTransfer): ScreeningStatus
}
