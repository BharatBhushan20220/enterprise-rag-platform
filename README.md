# Enterprise RAG Platform

Java 21 / Spring Boot 3 multi-module microservices platform for an enterprise AI knowledge assistant.

## Modules

| Module | Port | Description |
|---|---|---|
| `common-library` | — | Shared DTOs, exceptions, BaseEntity, exception handler auto-config |
| `api-gateway` | 8080 | Spring Cloud Gateway edge routing |
| `auth-service` | 8081 | Register / login / JWT / current user profile |
| `document-service` | 8082 | Document metadata registration |
| `embedding-service` | 8083 | Embedding generation (local deterministic stub) |
| `search-service` | 8084 | Chunk indexing + keyword search (vector-ready schema) |
| `chat-service` | 8085 | RAG ask + session history (retrieves from search-service) |

## Prerequisites

- JDK 21+
- Maven 3.9+ (or use `./mvnw`)
- Docker (optional, for full stack)

## Build & test

```bash
./mvnw clean test
```

## Run locally (without Docker)

1. Start Postgres and create DBs (or use `docker compose` for Postgres only):

```bash
docker compose -f docker/docker-compose.yml up postgres -d
```

2. Run services (separate terminals):

```bash
./mvnw -pl auth-service spring-boot:run
./mvnw -pl document-service spring-boot:run
./mvnw -pl embedding-service spring-boot:run
./mvnw -pl search-service spring-boot:run
./mvnw -pl chat-service spring-boot:run
./mvnw -pl api-gateway spring-boot:run
```

## Run full stack with Docker

```bash
docker compose -f docker/docker-compose.yml up --build
```

Gateway: `http://localhost:8080`

## Key auth APIs

```bash
# Register
curl -s http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Bharat","lastName":"Dev","email":"bharat@example.com","password":"password123"}'

# Login
curl -s http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"bharat@example.com","password":"password123"}'

# Me (Bearer token required)
curl -s http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer <token>"
```

## Configuration

Sensitive values are externalized via environment variables:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`, `JWT_EXPIRATION`
- `CORS_ALLOWED_ORIGINS`
- Service URLs for gateway / chat (`AUTH_SERVICE_URL`, `SEARCH_SERVICE_URL`, ...)

## Architecture

```
Client → api-gateway:8080
            ├─ /api/v1/auth/**        → auth-service:8081
            ├─ /api/v1/documents/**   → document-service:8082
            ├─ /api/v1/embeddings/**  → embedding-service:8083
            ├─ /api/v1/search/**      → search-service:8084
            └─ /api/v1/chat/**        → chat-service:8085
                                          └─ retrieves from search-service
```

PostgreSQL databases: `auth_db`, `document_db`, `search_db`, `chat_db`.
