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

## Frontend (local)

```bash
# terminal 1: backend stack (or at least gateway + services)
docker compose -f docker/docker-compose.yml up --build

# terminal 2: UI
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. Vite proxies `/api` → gateway `:8080`.

## Full Docker UI

Frontend is included in Compose on **http://localhost:8088** (nginx → gateway).

Bootstrap admin: `admin@example.com` / `Admin@12345`

## UI features

- Sign in / register / forgot + reset password
- Chat with session history
- Document upload (PDF/text) + library status
- Admin user role/status management
