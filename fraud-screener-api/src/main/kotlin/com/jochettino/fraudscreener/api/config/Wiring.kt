package com.jochettino.fraudscreener.api.config

import com.jochettino.fraudscreener.api.adapter.out.MockFraudScreening
import com.jochettino.fraudscreener.api.adapter.out.MockInstrumentLookup
import com.jochettino.fraudscreener.api.application.port.`in`.ScreenOutboundBankTransferUseCase
import com.jochettino.fraudscreener.api.application.service.ScreenOutboundBankTransferService

fun screenOutboundBankTransferUseCase(): ScreenOutboundBankTransferUseCase =
    ScreenOutboundBankTransferService(MockInstrumentLookup(), MockFraudScreening())
