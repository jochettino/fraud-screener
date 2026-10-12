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
// shortcut: configurations that cannot resolve standalone are skipped silently, revisit if a library still downloads late.
tasks.register("resolveDependencies") {
    doLast {
        configurations.filter { it.isCanBeResolved }.forEach { runCatching { it.resolve() } }
    }
}
