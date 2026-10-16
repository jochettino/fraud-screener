# CLAUDE.md

> **Initial version.** Brief on purpose; to be updated in the next tickets.

Multi-module Gradle (Kotlin DSL) project. Dependencies are managed in `gradle/libs.versions.toml`.

## Commands

- Build: `./gradlew build`
- Test: `./gradlew test` (unit tests only; excludes `integrationTest`)
- Integration test: `./gradlew integrationTest` (needs Docker; builds the image and checks `/health` and non-root)
- Run API: `./gradlew :fraud-screener-api:run` (port from env `PORT`, default 8080)
- Docker: `docker build -t fraud-screener-api .` then `docker run -p 8080:8080 fraud-screener-api`; health at `GET /health`
- CI: `.github/workflows/ci.yml` (single job `build-and-test`), PR-only, no push trigger

## Modules

- `fraud-screener-api`: Kotlin + Ktor/Netty, JDK 21 toolchain, base package `com.jochettino.fraudscreener.api`.

## API

- `POST /v1/banktransfer/outbound` and `POST /v1/banktransfer/inbound` (same request shape and rules; both instruments looked up) (mocked screening/lookup): 200 `{status: PASSED|REJECTED|HELD}`, 404 unknown instrument, 400 bad format. Mock rules: instrument ids starting with `00000000-` not found; amount > 10000 HELD; reference containing `fraud` (case-insensitive) REJECTED; else PASSED.
- Swagger UI at `GET /swagger` (e.g. http://localhost:8080/swagger).
- Contract: `fraud-screener-api/src/main/resources/openapi/fraud-screener-api.yaml` is the source of truth. DTOs are generated into `build/generated/openapi` (package `adapter.http.dto`, task `openApiGenerate`, runs before `compileKotlin`); never hand-write them. They stay in the adapter layer; the package omits `in` because the generator does not escape it.

## Architecture (hexagonal)

Packages: `domain`, `application` (`port/in`, `port/out`, `service`), `adapter` (`in/http`, `out`), `config`.
Dependency rule: `domain` depends on nothing; `application` depends only on `domain`; `adapter` depends on `application` and `domain`; `config` wires everything.
