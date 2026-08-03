CREATE TABLE chat_messages (
    id UUID PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    question VARCHAR(4000) NOT NULL,
    answer VARCHAR(8000) NOT NULL,
    sources VARCHAR(8000),
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP
);

CREATE INDEX idx_chat_messages_session_id ON chat_messages (session_id);
