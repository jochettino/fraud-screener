plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.openapi.generator)
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
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.swagger)
    implementation(libs.logback.classic)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.test.host)
}

// The OpenAPI spec is the source of truth; only the DTO models are generated (kept in the http adapter package).
openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$projectDir/src/main/resources/openapi/fraud-screener-api.yaml")
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.path)
    modelPackage.set("com.jochettino.fraudscreener.api.adapter.http.dto")
    globalProperties.set(mapOf("models" to "", "modelDocs" to "false", "modelTests" to "false"))
    typeMappings.set(mapOf("UUID" to "kotlin.String")) // validated in the adapter instead of needing a UUID serializer
    configOptions.set(mapOf("serializationLibrary" to "kotlinx_serialization"))
}

sourceSets.main {
    kotlin.srcDir(layout.buildDirectory.dir("generated/openapi/src/main/kotlin"))
}

tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
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
