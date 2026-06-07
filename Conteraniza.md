# Docker Setup Plan — Patient Management Service

## Context

The project is a Spring Boot 4.0.6 / Java 17 / Maven application at `/mnt/mnt2/patient-manegment/patient-service`. It currently runs with an H2 in-memory database in development (`application.properties`) and has zero Docker setup. The goal is to containerize the app for production using PostgreSQL, while keeping the existing dev workflow completely untouched.

---

## Phase 1 — Spring Profile for Production

**Goal:** Add a production Spring profile that reads PostgreSQL connection details from environment variables.

### File to Create: `patient-service/src/main/resources/application-prod.properties`

```properties
spring.datasource.url=${DB_URL}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
```

Spring will only activate this file when `SPRING_PROFILES_ACTIVE=prod` is set. In that mode, `${DB_URL}` etc. are resolved from OS environment variables at startup.

**What stays unchanged:**
- `application.properties` — H2 config, port 4000, H2 console — completely untouched
- `H2ConsoleConfig.java` — untouched
- `data.sql` — untouched
- `pom.xml` — untouched (PostgreSQL driver already present as runtime dep)

### Success Criteria — Phase 1
- `application-prod.properties` exists with the correct keys
- `application.properties` is byte-for-byte identical to before
- Running `./mvnw spring-boot:run` locally still starts on H2 with no changes needed

---

## Phase 2 — Dockerfile (Multi-Stage Build)

**Goal:** Build a lean, production-ready Docker image for the Spring Boot app.

### File to Create: `patient-service/Dockerfile`

**Stage 1 — Builder**
- Base image: `maven:3.9-eclipse-temurin-17`
- Copy `pom.xml` and download dependencies first (layer cache optimization)
- Copy `src/` and run `mvn clean package -DskipTests`
- Output: `target/patient-service-*.jar`

**Stage 2 — Runtime**
- Base image: `eclipse-temurin:17-jre-alpine` (minimal JRE, ~85 MB)
- Create non-root user `appuser` (security best practice)
- Copy only the JAR from the builder stage
- Set `ENV SPRING_PROFILES_ACTIVE=prod` so the production profile is always active inside the container
- Expose port `4000`
- Entrypoint: `java -jar /app/patient-service.jar`

### Success Criteria — Phase 2
- `docker build -t patient-service ./patient-service` completes with exit code 0
- `docker images patient-service` shows the image (~200–300 MB)
- Image runs as non-root user (verifiable via `docker run --rm patient-service whoami` → `appuser`)

---

## Phase 3 — docker-compose.yml

**Goal:** Orchestrate the app container + PostgreSQL container for production deployment.

### File to Create: `docker-compose.yml` (at project root)

```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: ${DB_NAME}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME} -d ${DB_NAME}"]
      interval: 10s
      timeout: 5s
      retries: 5

  patient-service:
    build: ./patient-service
    ports:
      - "4000:4000"
    environment:
      DB_URL: jdbc:postgresql://db:5432/${DB_NAME}
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      db:
        condition: service_healthy

volumes:
  postgres_data:
```

Key decisions:
- `depends_on` with `service_healthy` ensures the app only starts after PostgreSQL is ready to accept connections
- `DB_URL` is constructed inside compose (uses the `db` service hostname), so `.env` only needs `DB_NAME`/`DB_USERNAME`/`DB_PASSWORD`
- Named volume `postgres_data` persists DB data across container restarts

### Success Criteria — Phase 3
- `docker compose config` validates without errors
- `docker compose up --build` starts both containers
- `docker compose ps` shows both `db` and `patient-service` as `running` / `healthy`
- App logs show `Started PatientServiceApplication` with no DB connection errors

---

## Phase 4 — .env.example

**Goal:** Provide a template for the real `.env` file (which is already gitignored).

### File to Create: `.env.example` (at project root)

```
# Copy this file to .env and fill in your values
DB_USERNAME=postgres
DB_PASSWORD=changeme
DB_NAME=patientdb
```

The real `.env` file is never committed (`.gitignore` already excludes `.env*`).

### Success Criteria — Phase 4
- `.env.example` exists at project root
- `cp .env.example .env` + edit gives a working `.env`

---

## End-to-End Verification

### Verify Dev Setup (must still work unchanged)
```bash
cd patient-service
./mvnw spring-boot:run
# Expected: app starts on port 4000, H2 in-memory DB, no env vars needed
curl http://localhost:4000/patients
# Expected: JSON array of patients (seeded from data.sql)
```

### Verify Production Docker Setup
```bash
# 1. Create real .env from template
cp .env.example .env
# (edit .env if needed — defaults work out of the box)

# 2. Build and start
docker compose up --build

# 3. Check containers are healthy
docker compose ps
# Expected: both services show as running/healthy

# 4. Test the API
curl http://localhost:4000/patients
# Expected: JSON array of patients

curl -X POST http://localhost:4000/patients \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","address":"123 St","dateOfBirth":"1990-01-01","registeredDate":"2024-01-01"}'
# Expected: 201 Created with patient JSON

# 5. Verify data persists across restarts
docker compose down
docker compose up -d
curl http://localhost:4000/patients
# Expected: same patients still present (PostgreSQL volume persisted)
```

### Verify Image is Non-Root
```bash
docker compose run --rm patient-service whoami
# Expected: appuser
```

---

## File Summary

| File | Action | Notes |
|------|--------|-------|
| `patient-service/src/main/resources/application-prod.properties` | **CREATE** | PostgreSQL config via env vars |
| `patient-service/Dockerfile` | **CREATE** | Multi-stage Maven + JRE build |
| `docker-compose.yml` | **CREATE** | App + PostgreSQL orchestration |
| `.env.example` | **CREATE** | Template for secrets |
| `application.properties` | **NO CHANGE** | Dev H2 config stays intact |
| All existing Java files | **NO CHANGE** | Zero code changes needed |
