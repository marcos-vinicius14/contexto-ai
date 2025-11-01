CREATE EXTENSION IF NOT EXISTS vector;

CREATE TYPE document_status AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED');

ALTER TABLE tb_documents
    ADD COLUMN IF NOT EXISTS original_file_name VARCHAR(400),
    ADD COLUMN IF NOT EXISTS file_size BIGINT,
    ADD COLUMN IF NOT EXISTS content_type VARCHAR(100),
    ADD COLUMN IF NOT EXISTS extracted_text TEXT,
    ADD COLUMN IF NOT EXISTS embedding vector(768),
    ADD COLUMN IF NOT EXISTS error_message TEXT,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS processed_at TIMESTAMP WITH TIME ZONE;


CREATE INDEX IF NOT EXISTS idx_documents_user_id ON tb_documents(user_id);
CREATE INDEX IF NOT EXISTS idx_documents_status ON tb_documents(status);
CREATE INDEX IF NOT EXISTS idx_documents_created_at ON tb_documents(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_documents_embedding_hnsw ON tb_documents
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_documents_updated_at ON tb_documents;
CREATE TRIGGER update_documents_updated_at
    BEFORE UPDATE ON tb_documents
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
