#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PSQL_USER="${DB_USERNAME:-postgres}"

echo "Creating local databases with user: ${PSQL_USER}"
psql -U "${PSQL_USER}" -d postgres -f "${ROOT_DIR}/postgres/local-init.sql"

echo "Done. Start stack with:"
echo "  docker compose -f docker/docker-compose.yml -f docker/docker-compose.local-db.yml up --build"
