CREATE TABLE historico_os (
    id               BIGSERIAL   PRIMARY KEY,
    os_id            BIGINT      NOT NULL REFERENCES ordens_de_servico(id) ON DELETE CASCADE,
    status_anterior  VARCHAR(30),          -- null = criação da OS
    status_novo      VARCHAR(30) NOT NULL,
    usuario_username VARCHAR(80) NOT NULL,
    observacao       TEXT,
    timestamp        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_historico_os_os_id ON historico_os (os_id);
