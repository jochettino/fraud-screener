package com.jochettino.fraudscreener.api

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DockerImageIT {
    private lateinit var container: GenericContainer<*>

    @BeforeAll
    fun start() {
        val root = Path.of(System.getProperty("projectRoot"))
        val image = ImageFromDockerfile()
            .withFileFromPath("Dockerfile", root.resolve("Dockerfile"))
            // 0b755 keeps gradlew executable regardless of the host file system
            .withFileFromPath("gradlew", root.resolve("gradlew"), 0b111_101_101)
        listOf(
            "settings.gradle.kts", "build.gradle.kts", "gradle.properties", "gradle",
            "fraud-screener-api/build.gradle.kts", "fraud-screener-api/src",
        ).forEach { image.withFileFromPath(it, root.resolve(it)) }

        container = GenericContainer(image)
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/health").forStatusCode(200).withStartupTimeout(Duration.ofSeconds(120)))
        container.start()
    }

    @AfterAll
    fun stop() {
        if (::container.isInitialized) container.stop()
    }

    @Test
    fun `health endpoint returns UP`() {
        val request = HttpRequest.newBuilder(URI("http://${container.host}:${container.getMappedPort(8080)}/health")).build()
        val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(200, response.statusCode(), container.logs)
        assertEquals("""{"status":"UP"}""", response.body(), container.logs)
    }

    @Test
    fun `container runs as non-root`() {
        val uid = container.execInContainer("id", "-u").stdout.trim()
        assertNotEquals("0", uid, container.logs)
    }
}
