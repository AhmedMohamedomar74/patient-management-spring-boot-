# Patient Management Service

A RESTful microservice for managing patient records, built with Spring Boot and containerized with Docker.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Persistence | Spring Data JPA + Hibernate |
| Database (prod) | PostgreSQL 16 |
| Database (dev) | H2 (in-memory) |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven 3.9 |
| Container | Docker (multi-stage build) |
| Orchestration | Docker Compose |

## Architecture

The service follows a layered architecture:

```
Controller  →  Service  →  Repository  →  Database
    ↓              ↓
   DTO          Mapper
    ↓
GlobalExceptionHandler
```

- **Controller** (`PatinetController`) — handles HTTP routing under `/patients`
- **Service** (`PatientService`) — business logic, duplicate email guard
- **Repository** (`PatientRepositry`) — Spring Data JPA interface
- **Mapper** (`PatientMapper`) — converts between `Patient` entity and DTOs
- **DTOs** — `PatientRequestDto`, `PatientUpdateRequestDto`, `PatientResponseDto`
- **GlobalExceptionHandler** — `@ControllerAdvice` that maps domain exceptions to HTTP responses

## API Endpoints

| Method | Path | Description | Status |
|---|---|---|---|
| `GET` | `/patients` | List all patients | `200 OK` |
| `GET` | `/patients/{id}` | Get patient by UUID | `200 OK` |
| `POST` | `/patients` | Create a new patient | `201 Created` |
| `PUT` | `/patients/{id}` | Update a patient | `200 OK` |
| `DELETE` | `/patients/{id}` | Delete a patient | `204 No Content` |

### Patient Schema

```json
{
  "name":           "string (2–100 chars, required)",
  "email":          "string (valid email, unique, required)",
  "address":        "string (2–100 chars, required)",
  "dateOfBirth":    "string (ISO date: YYYY-MM-DD, required)",
  "registeredDate": "string (ISO date: YYYY-MM-DD, required)"
}
```

### Error Responses

| Exception | HTTP Status | Trigger |
|---|---|---|
| `MethodArgumentNotValidException` | `400 Bad Request` | Bean validation failure — returns field-level error map |
| `EmailAreadyExistException` | `400 Bad Request` | Duplicate email on create or update |
| `PatientNotFoundException` | `400 Bad Request` | No patient found for the given UUID |

## Data Model

```
patient
├── id             UUID  (PK, auto-generated)
├── name           VARCHAR  NOT NULL
├── email          VARCHAR  NOT NULL, UNIQUE
├── address        VARCHAR  NOT NULL
├── dateOfBirth    DATE     NOT NULL
└── registeredDate DATE     NOT NULL
```

## Spring Profiles

| Profile | Datasource | Activated by |
|---|---|---|
| `default` (dev) | H2 in-memory (`jdbc:h2:mem:testdb`) | Running locally without a profile |
| `prod` | PostgreSQL (env vars) | `SPRING_PROFILES_ACTIVE=prod` |

## Running Locally (Dev)

No external dependencies required — uses the embedded H2 database.

```bash
cd patient-service
./mvnw spring-boot:run
```

Service starts on **port 4000**.

## Running with Docker Compose (Prod)

1. Copy the env example and fill in your values:
   ```bash
   cp .env.example patient-service/.env
   # edit patient-service/.env
   ```

2. Start the stack:
   ```bash
   docker compose up --build
   ```

The service is exposed on **port 4005** (host) → **4000** (container).  
PostgreSQL data is persisted in the `postgres_data` named volume.

### Environment Variables

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL (set automatically by Compose to the `db` service) |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `DB_NAME` | Database name |

## Docker Image

The Dockerfile uses a two-stage build:

1. **Builder stage** (`maven:3.9-eclipse-temurin-17`) — resolves dependencies offline, then packages the fat JAR with `mvn clean package -DskipTests`.
2. **Runtime stage** (`eclipse-temurin:17-jre-alpine`) — copies only the JAR, runs as a non-root user (`appuser`) for security.

## Project Structure

```
patient-manegment/
├── docker-compose.yml
├── .env.example
└── patient-service/
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/com/pm/patientservice/
        ├── PatientServiceApplication.java
        ├── controller/
        │   └── PatinetController.java
        ├── service/
        │   └── PatientService.java
        ├── model/
        │   └── patient.java
        ├── dto/
        │   ├── PatientRequestDto.java
        │   ├── PatientUpdateRequestDto.java
        │   └── PatientResponseDto.java
        ├── mapper/
        │   └── PatientMapper.java
        ├── repostiry/
        │   └── PatientRepositry.java
        └── exception/
            ├── GlobalExceptionHandler.java
            ├── EmailAreadyExistException.java
            └── PatientNotFoundException.java
```
