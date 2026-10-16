package com.jochettino.fraudscreener.api

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class SwaggerEndpointTest {
    @Test fun `swagger ui is served`() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.OK, client.get("/swagger").status)
    }
}