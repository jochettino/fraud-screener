# CLAUDE.md

> **Initial version.** Brief on purpose; to be updated in the next tickets.

Multi-module Gradle (Kotlin DSL) project. Dependencies are managed in `gradle/libs.versions.toml`.

## Commands

- Build: `./gradlew build`
- Test: `./gradlew test`
- Run API: `./gradlew :fraud-screener-api:run` (port from env `PORT`, default 8080)

## Modules

- `fraud-screener-api`: Kotlin + Ktor/Netty, JDK 21 toolchain, base package `com.jochettino.fraudscreener.api`.

## Architecture (hexagonal)

Packages: `domain`, `application` (`port/in`, `port/out`, `service`), `adapter` (`in/http`, `out`), `config`.
Dependency rule: `domain` depends on nothing; `application` depends only on `domain`; `adapter` depends on `application` and `domain`; `config` wires everything.
