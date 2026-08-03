ALTER TABLE documents ADD COLUMN extracted_text TEXT;
ALTER TABLE documents ADD COLUMN chunk_count INT NOT NULL DEFAULT 0;
