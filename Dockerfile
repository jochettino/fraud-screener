FROM eclipse-temurin:21-jdk AS build
WORKDIR /src

# Dependency layer: build scripts only, so source changes do not bust the cache
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY fraud-screener-api/build.gradle.kts ./fraud-screener-api/
RUN chmod +x gradlew && ./gradlew --no-daemon help

COPY fraud-screener-api/src ./fraud-screener-api/src
RUN ./gradlew :fraud-screener-api:installDist --no-daemon

FROM eclipse-temurin:21-jre
RUN groupadd --system app && useradd --system --gid app --no-create-home app
WORKDIR /app
COPY --from=build /src/fraud-screener-api/build/install/fraud-screener-api /app
USER app
ENV PORT=8080 \
    JAVA_OPTS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["/app/bin/fraud-screener-api"]
