# fraud-screener

> **Initial version.** This README is intentionally brief and will be updated in the next tickets.

Fraud screening service. Currently only a skeleton: the `fraud-screener-api` module starts a Ktor (Netty) server with no routes.

## Prerequisites

- JDK 21 (Gradle toolchain; auto-provisioned if missing)

## Commands

```
./gradlew build
./gradlew test
./gradlew :fraud-screener-api:run   # listens on $PORT, default 8080
```

## Layout

- `fraud-screener-api` - Kotlin/Ktor service, hexagonal packages under `com.jochettino.fraudscreener.api`:
  `domain`, `application` (`port/in`, `port/out`, `service`), `adapter` (`in/http`, `out`), `config`.
