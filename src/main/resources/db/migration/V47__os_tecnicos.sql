CREATE TABLE os_tecnicos (
    os_id            BIGINT      NOT NULL REFERENCES ordens_de_servico(id) ON DELETE CASCADE,
    tecnico_username VARCHAR(80) NOT NULL,
    assigned_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (os_id, tecnico_username)
);

-- Índice reverso para buscar todas as OS de um técnico.
CREATE INDEX idx_os_tecnicos_tecnico_username ON os_tecnicos (tecnico_username);
