# Enterprise RAG Platform

Java 21 / Spring Boot 3 multi-module microservices platform for an enterprise AI knowledge assistant.

## Modules

| Module | Port | Description |
|---|---|---|
| `common-library` | — | Shared DTOs, exceptions, BaseEntity, correlation filter, vector utils |
| `api-gateway` | 8080 | Edge routing + JWT validation |
| `auth-service` | 8081 | Register / login / refresh / logout / JWT /me |
| `document-service` | 8082 | Multipart upload, PDF/text extract, chunk, embed, index |
| `embedding-service` | 8083 | Stub or OpenAI embeddings |
| `search-service` | 8084 | Chunk index + cosine similarity search (pgvector-ready) |
| `chat-service` | 8085 | RAG ask (retrieve + LLM stub/OpenAI) + session history |

## Build & test

```bash
./mvnw clean test
```

## Run with Docker

```bash
docker compose -f docker/docker-compose.yml up --build
```

Optional OpenAI:

```bash
export OPENAI_API_KEY=sk-...
# then set EMBEDDING_PROVIDER=openai and LLM_PROVIDER=openai in compose/env
```

## End-to-end flow

1. Register/login via gateway `POST /api/v1/auth/login`
2. Upload document `POST /api/v1/documents/upload` (multipart) with Bearer token
3. Ask `POST /api/v1/chat/ask` with `{ "sessionId": "...", "question": "..." }`

## Auth extras

- `POST /api/v1/auth/refresh` — rotate refresh token
- `POST /api/v1/auth/logout` — revoke refresh + blacklist access token
- Login/register rate limited (20 req/min/IP)

## Configuration highlights

- `JWT_SECRET`, `DB_*`, `OPENAI_API_KEY`
- `EMBEDDING_PROVIDER=stub|openai`
- `LLM_PROVIDER=stub|openai`
- `GATEWAY_JWT_ENABLED=true`
