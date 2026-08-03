-- Make V3 resilient when embedding_json cannot cast cleanly
CREATE EXTENSION IF NOT EXISTS vector;

ALTER TABLE search_chunks
    ADD COLUMN IF NOT EXISTS embedding vector(384);

DO $$
BEGIN
    UPDATE search_chunks
    SET embedding = embedding_json::vector
    WHERE embedding IS NULL
      AND embedding_json IS NOT NULL
      AND embedding_json <> '[]';
EXCEPTION
    WHEN others THEN
        RAISE NOTICE 'Skipped embedding_json backfill: %', SQLERRM;
END $$;

CREATE INDEX IF NOT EXISTS idx_search_chunks_embedding_ivfflat
    ON search_chunks
    USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);
