# Enterprise RAG Platform

Java 21 / Spring Boot 3 multi-module microservices platform for an enterprise AI knowledge assistant.

## Modules

| Module | Port | Description |
|---|---|---|
| `common-library` | — | Shared DTOs, exceptions, BaseEntity, correlation filter, vector utils |
| `api-gateway` | 8080 | Edge routing + JWT validation |
| `auth-service` | 8081 | Auth, refresh/logout, email verify, password reset, admin APIs |
| `document-service` | 8082 | Multipart upload, PDF/text extract, chunk, embed, index |
| `embedding-service` | 8083 | Stub or OpenAI embeddings |
| `search-service` | 8084 | Cosine search + native pgvector IVFFlat |
| `chat-service` | 8085 | RAG ask (retrieve + LLM stub/OpenAI) + session history |

## Build & test

```bash
./mvnw clean test
```

## Run with Docker

```bash
docker compose -f docker/docker-compose.yml up --build
```

Includes Postgres (pgvector), Redis, and all services.

Bootstrap admin (Docker default):
- email: `admin@example.com`
- password: `Admin@12345`

## Auth APIs

- `POST /api/v1/auth/register|login|refresh|logout`
- `POST /api/v1/auth/verify-email?token=`
- `POST /api/v1/auth/resend-verification`
- `POST /api/v1/auth/forgot-password`
- `POST /api/v1/auth/reset-password`
- `GET  /api/v1/admin/users` (ADMIN)
- `PATCH /api/v1/admin/users/{id}/role|status` (ADMIN)

## Useful env flags

- `REDIS_ENABLED=true` + `REDIS_HOST`
- `SEARCH_PGVECTOR_ENABLED=true`
- `AUTH_REQUIRE_EMAIL_VERIFICATION=true`
- `MAIL_PROVIDER=logging|smtp`
- `EMBEDDING_PROVIDER` / `LLM_PROVIDER` = `stub|openai`
