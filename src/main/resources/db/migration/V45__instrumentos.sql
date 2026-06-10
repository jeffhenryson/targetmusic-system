CREATE TABLE instrumentos (
    id              BIGSERIAL    PRIMARY KEY,
    tipo            VARCHAR(30)  NOT NULL,
    marca           VARCHAR(100) NOT NULL,
    modelo          VARCHAR(100) NOT NULL,
    numero_de_serie VARCHAR(100),
    cor             VARCHAR(50),
    descricao       TEXT,
    cliente_id      BIGINT       NOT NULL REFERENCES clientes(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_instrumentos_cliente_id ON instrumentos (cliente_id);
