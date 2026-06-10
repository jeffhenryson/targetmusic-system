CREATE TABLE pecas (
    id                 BIGSERIAL     PRIMARY KEY,
    nome               VARCHAR(150)  NOT NULL,
    descricao          TEXT,
    quantidade_estoque INTEGER       NOT NULL DEFAULT 0 CHECK (quantidade_estoque >= 0),
    preco_unitario     NUMERIC(10,2) NOT NULL,
    ativo              BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pecas_nome  ON pecas (nome);
CREATE INDEX idx_pecas_ativo ON pecas (ativo);
