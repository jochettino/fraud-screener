plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.jochettino.fraudscreener.api.ApplicationKt")
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.test.host)
}

tasks.test {
    useJUnitPlatform()
}

// Warms the Gradle cache from build scripts alone (used by the Dockerfile dependency layer).
// shortcut: other configurations that cannot resolve standalone are logged and skipped, revisit if a library still downloads late.
tasks.register("resolveDependencies") {
    val required = setOf("runtimeClasspath", "compileClasspath", "testRuntimeClasspath")
    doLast {
        configurations.filter { it.isCanBeResolved }.forEach {
            if (it.name in required) {
                it.resolve()
            } else {
                runCatching { it.resolve() }.onFailure { e -> logger.warn("skipped ${it.name}: ${e.message}") }
            }
        }
    }
}
