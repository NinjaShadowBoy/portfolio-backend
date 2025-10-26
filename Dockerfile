# Multi-stage Dockerfile for Spring Boot (Kotlin) app
# Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder

# Install packages required by Gradle wrapper (unzip)
RUN apk add --no-cache bash unzip

WORKDIR /app

# Only copy gradle config first to leverage Docker layer caching
COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Pre-download dependencies
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies > /dev/null || true

# Now copy the source
COPY src src

# Build the application (skip tests for faster builds; adjust if you want tests)
RUN ./gradlew --no-daemon clean bootJar -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Create a non-root user
RUN addgroup -S app && adduser -S app -G app

# Copy the fat jar from the builder stage
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Ensure upload directories exist (these can be overridden by env properties)
RUN mkdir -p /data/uploads/photos /data/uploads/profilephoto && chown -R app:app /data

# Expose the default port (Render will route traffic to $PORT; we map via JAVA_TOOL_OPTIONS)
EXPOSE 8080

USER app

# Healthcheck (optional; if you expose actuator, change path accordingly)
# HEALTHCHECK --interval=30s --timeout=5s --start-period=30s CMD wget -qO- http://localhost:8080/ || exit 1

# JVM memory and timezone tuning can be added via JAVA_OPTS env var at deploy time
# Bind Spring Boot to Render's PORT env var at runtime
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT ${JAVA_OPTS} -jar /app/app.jar"]
