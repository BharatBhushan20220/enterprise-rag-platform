-- Run against your local Postgres (once):
--   psql -U postgres -f docker/postgres/local-init.sql
--
-- If your user/db differs:
--   psql -U <user> -d postgres -f docker/postgres/local-init.sql

SELECT 'CREATE DATABASE auth_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'auth_db')\gexec

SELECT 'CREATE DATABASE chat_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'chat_db')\gexec

SELECT 'CREATE DATABASE document_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'document_db')\gexec

SELECT 'CREATE DATABASE search_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'search_db')\gexec

-- Optional (only if pgvector is installed on local Postgres):
-- \c search_db
-- CREATE EXTENSION IF NOT EXISTS vector;
