-- Permissões de negócio do Target Music (CLIENTE_*, INSTRUMENTO_*, OS_*)

INSERT INTO permissions (name) VALUES
    ('CLIENTE_CREATE'),
    ('CLIENTE_READ'),
    ('CLIENTE_UPDATE'),
    ('CLIENTE_DELETE'),
    ('INSTRUMENTO_CREATE'),
    ('INSTRUMENTO_READ'),
    ('INSTRUMENTO_UPDATE'),
    ('INSTRUMENTO_DELETE'),
    ('OS_CREATE'),
    ('OS_READ'),
    ('OS_UPDATE'),
    ('OS_STATUS'),
    ('OS_DELETE'),
    ('OS_ASSIGN_TECNICO'),
    ('OS_ORCAMENTO'),
    ('OS_ORCAMENTO_APROVAR'),
    ('OS_ORCAMENTO_RECUSAR'),
    ('OS_ENTREGA')
ON CONFLICT (name) DO NOTHING;
