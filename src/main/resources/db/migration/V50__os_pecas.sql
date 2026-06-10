CREATE TABLE os_pecas (
    id               BIGSERIAL     PRIMARY KEY,
    os_id            BIGINT        NOT NULL REFERENCES ordens_de_servico(id),
    peca_id          BIGINT        NOT NULL REFERENCES pecas(id),
    peca_nome        VARCHAR(150)  NOT NULL,   -- snapshot: preserva histórico se peça for renomeada
    quantidade       INTEGER       NOT NULL CHECK (quantidade > 0),
    preco_unitario   NUMERIC(10,2) NOT NULL,   -- snapshot do preço no momento do uso
    tecnico_username VARCHAR(80)   NOT NULL,
    added_at         TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_os_pecas_os_id   ON os_pecas (os_id);
CREATE INDEX idx_os_pecas_peca_id ON os_pecas (peca_id);
