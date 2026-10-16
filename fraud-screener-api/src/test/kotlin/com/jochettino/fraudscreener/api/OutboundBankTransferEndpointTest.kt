package com.jochettino.fraudscreener.api

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class OutboundBankTransferEndpointTest {
    private val ok = "11111111-1111-1111-1111-111111111111"
    private val unknown = "00000000-0000-0000-0000-000000000001"

    private fun body(
        from: String = "\"$ok\"",
        to: String = "\"$ok\"",
        currency: String = "\"EUR\"",
        amount: String = "\"23.5\"",
        reference: String? = null,
    ) = """{"fromInstrumentId":$from,"toInstrumentId":$to,"money":{"currency":$currency,"amount":$amount}""" +
        (reference?.let { ""","reference":"$it"""" } ?: "") + "}"

    private fun post(json: String, expected: HttpStatusCode, expectedBody: String? = null) = testApplication {
        application { module() }
        send(json, expected, expectedBody)
    }

    private suspend fun ApplicationTestBuilder.send(json: String, expected: HttpStatusCode, expectedBody: String?) {
        val response = client.post("/v1/banktransfer/outbound") {
            contentType(ContentType.Application.Json)
            setBody(json)
        }
        assertEquals(expected, response.status, response.bodyAsText())
        expectedBody?.let { assertEquals(it, response.bodyAsText()) }
    }

    @Test fun `passed`() = post(body(), HttpStatusCode.OK, """{"status":"PASSED"}""")

    @Test fun `passed with ok reference`() = post(body(reference = "salary"), HttpStatusCode.OK, """{"status":"PASSED"}""")

    @Test fun `held above threshold`() = post(body(amount = "\"10000.01\""), HttpStatusCode.OK, """{"status":"HELD"}""")

    @Test fun `rejected by reference`() = post(body(reference = "Fraud!"), HttpStatusCode.OK, """{"status":"REJECTED"}""")

    @Test fun `unknown from instrument is 404`() = post(body(from = "\"$unknown\""), HttpStatusCode.NotFound)

    @Test fun `unknown to instrument is 404`() = post(body(to = "\"$unknown\""), HttpStatusCode.NotFound)

    @Test fun `invalid uuid is 400`() = post(body(from = "\"nope\""), HttpStatusCode.BadRequest)

    @Test fun `non-canonical uuid is 400 for both ids`() {
        post(body(from = "\"1-1-1-1-1\""), HttpStatusCode.BadRequest)
        post(body(to = "\"1-1-1-1-1\""), HttpStatusCode.BadRequest)
    }

    @Test fun `bad currency is 400`() {
        post(body(currency = "\"eur\""), HttpStatusCode.BadRequest)
        post(body(currency = "\"978\""), HttpStatusCode.BadRequest)
    }

    @Test fun `bad amount is 400`() {
        listOf("\"abc\"", "\"0\"", "\"-1\"").forEach { post(body(amount = it), HttpStatusCode.BadRequest) }
    }

    @Test fun `reference of 21 chars is 400`() = post(body(reference = "a".repeat(21)), HttpStatusCode.BadRequest)

    @Test fun `reference of 20 chars is ok`() = post(body(reference = "a".repeat(20)), HttpStatusCode.OK)

    @Test fun `missing required field is 400`() =
        post("""{"fromInstrumentId":"$ok","money":{"currency":"EUR","amount":"1"}}""", HttpStatusCode.BadRequest)

    @Test fun `malformed json is 400`() = post("{not json", HttpStatusCode.BadRequest)
}
