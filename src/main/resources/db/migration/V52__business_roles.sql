-- Roles de negócio: ROLE_ATENDENTE, ROLE_TECNICO, ROLE_CLIENTE

INSERT INTO roles (name) VALUES
    ('ROLE_ATENDENTE'),
    ('ROLE_TECNICO'),
    ('ROLE_CLIENTE')
ON CONFLICT (name) DO NOTHING;

-- ROLE_ATENDENTE: recepção, comunicação com cliente, gestão operacional de OS
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_ATENDENTE'
  AND p.name IN (
    'CLIENTE_CREATE', 'CLIENTE_READ', 'CLIENTE_UPDATE',
    'INSTRUMENTO_CREATE', 'INSTRUMENTO_READ', 'INSTRUMENTO_UPDATE',
    'OS_CREATE', 'OS_READ', 'OS_UPDATE', 'OS_STATUS',
    'OS_ASSIGN_TECNICO', 'OS_ORCAMENTO_APROVAR', 'OS_ORCAMENTO_RECUSAR', 'OS_ENTREGA'
  )
ON CONFLICT DO NOTHING;

-- ROLE_TECNICO: diagnóstico, manutenção, orçamento técnico
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_TECNICO'
  AND p.name IN (
    'CLIENTE_READ',
    'INSTRUMENTO_READ',
    'OS_READ', 'OS_UPDATE', 'OS_STATUS', 'OS_ORCAMENTO'
  )
ON CONFLICT DO NOTHING;

-- ROLE_CLIENTE: portal read-only (filtro de ownership aplicado no service)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_CLIENTE'
  AND p.name IN (
    'OS_READ',
    'INSTRUMENTO_READ'
  )
ON CONFLICT DO NOTHING;

-- ROLE_ADMIN herda todas as permissões de negócio
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_ADMIN'
  AND p.name IN (
    'CLIENTE_CREATE', 'CLIENTE_READ', 'CLIENTE_UPDATE', 'CLIENTE_DELETE',
    'INSTRUMENTO_CREATE', 'INSTRUMENTO_READ', 'INSTRUMENTO_UPDATE', 'INSTRUMENTO_DELETE',
    'OS_CREATE', 'OS_READ', 'OS_UPDATE', 'OS_STATUS', 'OS_DELETE',
    'OS_ASSIGN_TECNICO', 'OS_ORCAMENTO', 'OS_ORCAMENTO_APROVAR', 'OS_ORCAMENTO_RECUSAR', 'OS_ENTREGA'
  )
ON CONFLICT DO NOTHING;
