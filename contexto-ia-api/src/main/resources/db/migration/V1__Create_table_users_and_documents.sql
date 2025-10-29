CREATE TABLE tb_users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE tb_documents(
    id UUID PRIMARY KEY,
    file_name VARCHAR(400) NOT NULL,
    storage_key VARCHAR(1024) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    user_id UUID NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES tb_users(id)
);

CREATE TABLE tb_chat_history(
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL,
    content TEXT NOT NULL,
    role VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES tb_users(id)
);