# fraud-screener

> **Initial version.** This README is intentionally brief and will be updated in the next tickets.

Fraud screening service. Currently only a skeleton: the `fraud-screener-api` module starts a Ktor (Netty) server exposing only `GET /health`.

## Prerequisites

- JDK 21 (Gradle toolchain; auto-provisioned if missing)

## Commands

```
./gradlew build
./gradlew test
./gradlew :fraud-screener-api:run   # listens on $PORT, default 8080
```

## Run with Docker

```
docker build -t fraud-screener-api .
docker run -p 8080:8080 fraud-screener-api
curl localhost:8080/health   # {"status":"UP"}
```

## CI

GitHub Actions (`.github/workflows/ci.yml`) runs on pull requests to `main` (and manually). Job `build-and-test` runs `./gradlew build`; job `docker` builds the image, starts it, polls `/health` and checks it runs as non-root.

## Layout

- `fraud-screener-api` - Kotlin/Ktor service, hexagonal packages under `com.jochettino.fraudscreener.api`:
  `domain`, `application` (`port/in`, `port/out`, `service`), `adapter` (`in/http`, `out`), `config`.
