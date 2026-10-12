package com.jochettino.fraudscreener.api.config

import com.jochettino.fraudscreener.api.adapter.out.MockFraudScreening
import com.jochettino.fraudscreener.api.adapter.out.MockInstrumentLookup
import com.jochettino.fraudscreener.api.application.port.`in`.ScreenOutboundTransferUseCase
import com.jochettino.fraudscreener.api.application.service.ScreenOutboundTransferService

fun screenOutboundTransferUseCase(): ScreenOutboundTransferUseCase =
    ScreenOutboundTransferService(MockInstrumentLookup(), MockFraudScreening())
