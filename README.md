# Enterprise RAG Platform

Java 21 / Spring Boot 3 multi-module backend + React (Vite) frontend (**Aether**).

## Modules

| Module | Port | Description |
|---|---|---|
| `frontend` | 5173 (dev) / 8088 (docker) | React UI for auth, documents, chat, admin |
| `api-gateway` | 8080 | Edge routing + JWT validation |
| `auth-service` | 8081 | Auth, refresh/logout, verify/reset, admin APIs |
| `document-service` | 8082 | Upload/parse/chunk/index |
| `embedding-service` | 8083 | Stub or OpenAI embeddings |
| `search-service` | 8084 | Cosine + pgvector search |
| `chat-service` | 8085 | RAG ask + history |
| `common-library` | — | Shared types |

## Backend build

```bash
./mvnw clean test
```

## Run with local Postgres (recommended on Mac if :5432 is busy)

**1. Create databases once** (user/password apne local Postgres ke hisaab se):

```bash
psql -U postgres -d postgres -f docker/postgres/local-init.sql
```

Agar password prompt aaye / user alag ho:

```bash
psql -U <your_user> -d postgres -f docker/postgres/local-init.sql
```

If auth-service fails with Flyway errors (`non-empty schema`, `relation "users" already exists`,
failed migration), **stop containers that use the DB**, then reset:

```bash
# stop auth so it releases DB connections
docker stop rag-auth-service 2>/dev/null || true
psql -U postgres -d postgres -f docker/postgres/local-reset.sql
# or auth only:
# psql -U postgres -d postgres -c "DROP DATABASE IF EXISTS auth_db WITH (FORCE); CREATE DATABASE auth_db;"
```

**2. Start services (Docker Postgres skip / host DB):**

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=postgres   # apna local password
docker compose -f docker/docker-compose.yml -f docker/docker-compose.local-db.yml up --build
```

Yeh override Postgres container ko no-port stub bana deta hai, aur services
`host.docker.internal:5432` (local Mac Postgres) use karti hain.

**3. Frontend:**

```bash
cd frontend && npm install && npm run dev
```

Open `http://localhost:5173`.

Notes:
- Containers host DB ko `host.docker.internal:5432` se hit karte hain
- Redis abhi bhi Docker mein chalega
- Local pe pgvector na ho to theek hai: Flyway V3 skips the extension, and `SEARCH_PGVECTOR_ENABLED=false` (default) uses JSON cosine search

## Run with Docker Postgres

```bash
docker compose -f docker/docker-compose.yml up --build
```

Docker Postgres is published on host port **5433** (not 5432), so it won't clash with local Postgres.
## Full Docker UI

Frontend container: **http://localhost:8088**

Bootstrap admin: `admin@example.com` / `Admin@12345`

## UI features

- Sign in / register / forgot + reset password
- Chat with session history
- Document upload (PDF/text) + library status
- Admin user role/status management
