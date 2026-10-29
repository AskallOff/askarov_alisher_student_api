# Student API of Alisher Askarov (IT2-2304, ID 38226) - multi-stage build
#   docker build -t askarov-alisher-student-api:v1.0-askarov-alisher .

# ===== 1. builder: official JDK image, the JAR is built with the Gradle wrapper =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# build files first - this layer (Gradle + dependencies) is reused until build.gradle changes
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
# BuildKit cache mount: Gradle and the libraries are downloaded once and kept between builds
# (also between the builds started by Jenkins), so a build does not need the internet every time
RUN --mount=type=cache,target=/root/.gradle chmod +x gradlew && ./gradlew --no-daemon -q dependencies > /dev/null

COPY src src
# tests are run by Jenkins in a separate stage, here only the JAR is built
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon bootJar -x test

# ===== 2. runtime: only the Java runtime (JRE) and the JAR =====
FROM eclipse-temurin:21-jre-alpine AS runtime
LABEL org.opencontainers.image.title="askarov-alisher-student-api" \
      org.opencontainers.image.authors="Alisher Askarov" \
      student.group="IT2-2304" \
      student.id="38226"

# the application does not need root rights
RUN adduser -D -H -s /sbin/nologin askarov
WORKDIR /opt/app
COPY --from=builder /workspace/build/libs/askarov-alisher-student-api.jar app.jar
USER askarov

EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health > /dev/null || exit 1
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
