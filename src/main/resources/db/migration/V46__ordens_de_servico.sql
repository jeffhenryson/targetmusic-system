-- Sequência global para numeração das OS. Nunca reinicia.
-- Formato gerado pela aplicação: "OS-YYYY-NNNNN" onde NNNNN vem desta sequence.
CREATE SEQUENCE os_numero_seq START 1;

CREATE TABLE ordens_de_servico (
    id                 BIGSERIAL     PRIMARY KEY,
    numero             VARCHAR(20)   NOT NULL UNIQUE,
    status             VARCHAR(30)   NOT NULL,
    instrumento_id     BIGINT        NOT NULL REFERENCES instrumentos(id),
    cliente_id         BIGINT        NOT NULL REFERENCES clientes(id),
    atendente_username VARCHAR(80)   NOT NULL,
    descricao_problema TEXT          NOT NULL,
    laudo_tecnico      TEXT,
    valor_orcamento    NUMERIC(10,2),
    valor_final        NUMERIC(10,2),
    prazo_estimado     DATE,
    data_recebimento   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    data_entrega       TIMESTAMPTZ,
    observacoes        TEXT,
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_os_status        ON ordens_de_servico (status);
CREATE INDEX idx_os_cliente_id    ON ordens_de_servico (cliente_id);
CREATE INDEX idx_os_instrumento_id ON ordens_de_servico (instrumento_id);
