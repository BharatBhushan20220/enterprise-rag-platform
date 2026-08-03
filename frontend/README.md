# Aether Frontend

React + TypeScript + Vite UI for the Enterprise RAG Platform.

## Develop

```bash
npm install
npm run dev
```

Requires API gateway on `http://localhost:8080` (Vite proxies `/api`).

## Build

```bash
npm run build
npm run preview
```

## Environment

Optional:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

Leave empty in local/dev to use the Vite proxy / nginx relative `/api` paths.
