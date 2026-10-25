# Portfolio Backend (Kotlin / Spring Boot)

The original REST API behind my portfolio site, written in Kotlin with Spring
Boot. It serves the projects the site lists, their photos and ratings, takes
contact messages, and handles login.

> This version has since been rewritten in Go:
> [portfolio-backend-go](https://github.com/NinjaShadowBoy/portfolio-backend-go)
> is the one the live site uses. This repository is kept for reference.

What it does:

- **Projects and photos** (`/api/v1/projects`, `/api/v1/photos`): public reads,
  authenticated writes. Images are stored on Cloudinary.
- **Ratings** (`/api/v1/ratings`) left by signed-in users on projects.
- **Contact** (`/api/v1/contact`): messages are saved and emailed over SMTP.
- **Auth** (`/api/v1/auth`): email and password with JWTs, plus Google, GitHub
  and Facebook OAuth2 login.
- **API docs** with springdoc / Swagger UI.

## Stack

- Kotlin 1.9, Java 17
- Spring Boot 3.5 (Web, Data JPA, Security, OAuth2 client, Mail)
- PostgreSQL
- JJWT for tokens
- Gradle (wrapper included)

## Getting started

You need JDK 17 and a PostgreSQL server.

```bash
git clone https://github.com/NinjaShadowBoy/portfolio-backend.git
cd portfolio-backend
createdb portfolio            # or create the database any other way
./gradlew bootRun             # on Windows: gradlew.bat bootRun
```

The API starts on `http://localhost:8080/portfolio`, so routes look like
`http://localhost:8080/portfolio/api/v1/projects`. Swagger UI is at
`http://localhost:8080/portfolio/swagger-ui` (OpenAPI JSON at `/portfolio/api-docs`).

Hibernate creates and updates the tables itself (`ddl-auto=update`), so there
are no migrations to run.

### Configuration

Everything has a local default in `src/main/resources/application.properties`
and can be overridden with environment variables:

| Variable | Default | Purpose |
|---|---|---|
| `PORT` | `8080` | HTTP port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/portfolio` | Database |
| `DB_USER` / `DB_PASSWORD` | `postgres` / `123456789` | Database credentials |
| `JWT_SECRET` | a dev key | Signing key for JWTs, change it outside local use |
| `SMTP_USERNAME` / `SMTP_PASSWORD` | placeholders | Gmail SMTP account for contact emails |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | placeholders | Google login |
| `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET` | placeholders | GitHub login |
| `FACEBOOK_CLIENT_ID` / `FACEBOOK_CLIENT_SECRET` | placeholders | Facebook login |
| `OAUTH2_BASE_URL` | `http://localhost:8080/portfolio` | Base of the OAuth2 callback URLs |
| `OAUTH2_REDIRECT_URI` | `http://localhost:4200/oauth2/redirect` | Frontend page that receives the token |

`application-prod.properties` holds the production profile
(`SPRING_PROFILES_ACTIVE=prod`).

## Build and test

```bash
./gradlew test
./gradlew bootJar             # fat jar in build/libs/
java -jar build/libs/*.jar
```

## Docker

```bash
docker build -t portfolio-backend .
docker run -p 8080:8080 -e PORT=8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/portfolio \
  portfolio-backend
```

The image reads the port from `PORT`, which is how it ran on Render.

## Project layout

```
src/main/kotlin/com/ninjashadowboy/portfolio/
├── config/          security, JWT filter, OpenAPI, CORS
├── controllers/     REST controllers and their OpenAPI docs
├── dtos/            request and response types, entity mappers
├── entities/        JPA entities
├── repositories/    Spring Data repositories
├── security/oauth2/ OAuth2 user mapping and success/failure handlers
└── services/        business logic
```

The frontend that consumed it is
[portfolio-app](https://github.com/NinjaShadowBoy/portfolio-app).
