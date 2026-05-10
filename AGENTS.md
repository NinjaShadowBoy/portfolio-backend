# Portfolio Backend - Agent Guidelines

## Project Overview
Spring Boot 3.5.0 backend for portfolio application written in Kotlin.

## Essential Commands

**Build & Run**
- `./gradlew bootRun` - Run application locally
- `./gradlew clean build` - Build with tests
- `./gradlew clean bootJar -x test` - Build jar skipping tests (as in Dockerfile)
- `docker build -t portfolio-backend .` - Build Docker image
- `docker run -p 8080:8080 portfolio-backend` - Run Docker container

**Testing**
- `./gradlew test` - Run all tests
- `./gradlew test --tests "*.AuthControllerTest"` - Run specific test class

**Dependencies**
- `./gradlew dependencies` - List all dependencies
- `./gradlew --write-locks` - Update dependency lock files

## Code Organization
- Entry point: `src/main/kotlin/com/ninjashadowboy/portfolio/PortfolioApplication.kt`
- API Controllers: `src/main/kotlin/com/ninjashadowboy/portfolio/controllers/api/`
- API Documentation: `src/main/kotlin/com/ninjashadowboy/portfolio/controllers/docs/`
- Configuration: `src/main/kotlin/com/ninjashadowboy/portfolio/config/`

## Key Technologies
- Kotlin 1.9.25
- Spring Boot 3.5.0 (Web, Data JPA, Security, Mail, OAuth2 Client)
- PostgreSQL & MySQL drivers
- JWT authentication (jjwt)
- SpringDoc OpenAPI 2.5.0
- Cloudinary SDK for image management
- Spring Boot DevTools (implicit in testImplementation)

## Important Notes
- All API endpoints prefixed with `/portfolio` (via `server.servlet.context-path`)
  - Full path example: `/portfolio/api/v1/auth/login`
  - Swagger UI: `/portfolio/swagger-ui/index.html`
- Uses pure Kotlin data classes (Lombok intentionally omitted)
- Multi-stage Docker build with non-root user
- Java 17 toolchain configured
- Tests use JUnit 5 (via spring-boot-starter-test and kotlin-test-junit5)
- OpenAPI documentation generated via SpringDoc
- Health check endpoint available at actuator (if enabled)

## Environment Variables
- `PORT` - Used by Docker entrypoint for port binding (Render.com compatible)
- `JAVA_OPTS` - For JVM memory and timezone tuning
- Standard Spring Boot environment variables for datasource, JWT, email, etc.