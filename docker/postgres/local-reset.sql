-- Wipe and recreate local RAG databases.
-- Fixes Flyway errors like:
--   Found non-empty schema(s) "public" but no schema history table
--   ERROR: relation "users" already exists
--   Migration checksum mismatch / failed migration
--
-- WARNING: deletes all local auth/chat/document/search data.
--
--   psql -U postgres -d postgres -f docker/postgres/local-reset.sql

SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname IN ('auth_db', 'chat_db', 'document_db', 'search_db')
  AND pid <> pg_backend_pid();

DROP DATABASE IF EXISTS auth_db;
DROP DATABASE IF EXISTS chat_db;
DROP DATABASE IF EXISTS document_db;
DROP DATABASE IF EXISTS search_db;

CREATE DATABASE auth_db;
CREATE DATABASE chat_db;
CREATE DATABASE document_db;
CREATE DATABASE search_db;
