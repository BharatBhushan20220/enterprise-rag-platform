-- Optional pgvector setup. Succeeds even when the vector extension is not installed
-- (stock Mac/Homebrew/EDB Postgres). Search uses embedding_json cosine when
-- SEARCH_PGVECTOR_ENABLED=false; Docker Compose pgvector image applies this fully.

DO $migration$
BEGIN
    CREATE EXTENSION IF NOT EXISTS vector;

    ALTER TABLE search_chunks
        ADD COLUMN IF NOT EXISTS embedding vector(384);

    BEGIN
        UPDATE search_chunks
        SET embedding = embedding_json::vector
        WHERE embedding IS NULL
          AND embedding_json IS NOT NULL
          AND embedding_json <> '[]';
    EXCEPTION
        WHEN others THEN
            RAISE NOTICE 'Skipped embedding_json backfill: %', SQLERRM;
    END;

    BEGIN
        CREATE INDEX IF NOT EXISTS idx_search_chunks_embedding_ivfflat
            ON search_chunks
            USING ivfflat (embedding vector_cosine_ops)
            WITH (lists = 100);
    EXCEPTION
        WHEN others THEN
            RAISE NOTICE 'Skipped ivfflat index: %', SQLERRM;
    END;

EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'pgvector not available; using JSON cosine search fallback: %', SQLERRM;
END
$migration$;
