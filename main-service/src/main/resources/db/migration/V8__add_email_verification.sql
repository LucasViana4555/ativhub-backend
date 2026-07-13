-- Adiciona a coluna email_verified na tabela users
ALTER TABLE users ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT TRUE;

-- Cria a tabela de tokens de verificação de email
CREATE TABLE email_verification_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(6) NOT NULL,
    user_id UUID NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
