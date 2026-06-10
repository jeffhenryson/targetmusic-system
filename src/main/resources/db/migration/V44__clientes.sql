CREATE TABLE clientes (
    id          BIGSERIAL    PRIMARY KEY,
    nome        VARCHAR(150) NOT NULL,
    telefone    VARCHAR(20)  NOT NULL,
    email       VARCHAR(150),
    cpf         VARCHAR(14),
    endereco    VARCHAR(255),
    observacoes TEXT,
    user_id     BIGINT       REFERENCES users(id) ON DELETE SET NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_clientes_nome    ON clientes (nome);
CREATE INDEX idx_clientes_email   ON clientes (email);
CREATE INDEX idx_clientes_user_id ON clientes (user_id);
