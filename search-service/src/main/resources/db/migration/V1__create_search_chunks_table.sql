CREATE TABLE search_chunks (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    chunk_index INT NOT NULL,
    content VARCHAR(4000) NOT NULL,
    embedding_model VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP
);

CREATE INDEX idx_search_chunks_document_id ON search_chunks (document_id);
