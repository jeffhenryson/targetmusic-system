# 06 — Roles e Permissões — Target Music

Complementa a estrutura RBAC do template de segurança com os roles e permissões específicos do negócio.

## Roles do sistema

| Role | Descrição |
|------|-----------|
| `ROLE_ADMIN` | Gestão completa: usuários, relatórios, configuração do sistema |
| `ROLE_ATENDENTE` | Abre OS, recebe instrumentos, comunica orçamentos, registra entregas |
| `ROLE_TECNICO` | Diagnostica, atualiza laudo, altera status técnico, define orçamento |
| `ROLE_CLIENTE` | Portal de acompanhamento — visualiza apenas as próprias OS (read-only) |
| `ROLE_DEV` | Acesso elevado ao sistema operacional (herdado do template) |

## Permissões de negócio

### Clientes

| Permissão | Descrição |
|-----------|-----------|
| `CLIENTE_CREATE` | Cadastrar cliente |
| `CLIENTE_READ` | Consultar clientes |
| `CLIENTE_UPDATE` | Atualizar dados de cliente |
| `CLIENTE_DELETE` | Remover cliente |

### Instrumentos

| Permissão | Descrição |
|-----------|-----------|
| `INSTRUMENTO_CREATE` | Cadastrar instrumento |
| `INSTRUMENTO_READ` | Consultar instrumentos |
| `INSTRUMENTO_UPDATE` | Atualizar dados de instrumento |
| `INSTRUMENTO_DELETE` | Remover instrumento |

### Ordens de Serviço

| Permissão | Descrição |
|-----------|-----------|
| `OS_CREATE` | Abrir nova OS |
| `OS_READ` | Consultar OS (admin/atendente vê todas; técnico vê as suas; cliente vê as próprias) |
| `OS_UPDATE` | Atualizar campos da OS (laudo, observações, prazo) |
| `OS_STATUS` | Alterar status da OS (sujeito às regras de transição) |
| `OS_DELETE` | Cancelar/remover OS |
| `OS_ASSIGN_TECNICO` | Atribuir técnico a uma OS |
| `OS_ORCAMENTO` | Definir valor de orçamento |
| `OS_ORCAMENTO_APPROVE` | Aprovar ou recusar orçamento (ação do atendente após resposta do cliente) |
| `OS_ENTREGA` | Registrar entrega do instrumento |

## Matriz de permissões por role

| Permissão | ADMIN | ATENDENTE | TECNICO | CLIENTE |
|-----------|:-----:|:---------:|:-------:|:-------:|
| `CLIENTE_CREATE` | ✅ | ✅ | — | — |
| `CLIENTE_READ` | ✅ | ✅ | ✅ | — |
| `CLIENTE_UPDATE` | ✅ | ✅ | — | — |
| `CLIENTE_DELETE` | ✅ | — | — | — |
| `INSTRUMENTO_CREATE` | ✅ | ✅ | — | — |
| `INSTRUMENTO_READ` | ✅ | ✅ | ✅ | ✅ (próprios) |
| `INSTRUMENTO_UPDATE` | ✅ | ✅ | — | — |
| `INSTRUMENTO_DELETE` | ✅ | — | — | — |
| `OS_CREATE` | ✅ | ✅ | — | — |
| `OS_READ` | ✅ | ✅ | ✅ | ✅ (próprias) |
| `OS_UPDATE` | ✅ | ✅ | ✅ | — |
| `OS_STATUS` | ✅ | ✅ (RECEBIDO→EM_ANALISE, PRONTO→ENTREGUE) | ✅ (EM_ANALISE→AGUARDANDO_APROVACAO, EM_MANUTENCAO, AGUARDANDO_PECA→EM_MANUTENCAO, PRONTO) | — |
| `OS_DELETE` | ✅ | — | — | — |
| `OS_ASSIGN_TECNICO` | ✅ | ✅ | — | — |
| `OS_ORCAMENTO` | ✅ | — | ✅ | — |
| `OS_ORCAMENTO_APPROVE` | ✅ | ✅ | — | — |
| `OS_ENTREGA` | ✅ | ✅ | — | — |

> `ROLE_CLIENTE` acessa somente registros vinculados ao seu `clienteId` — filtro aplicado nos services, não apenas nas permissões.

## Migrations

As permissões de negócio são criadas a partir da migration **V44** (após as 43 migrations do template).
O bootstrap de roles (`DevRoleBootstrapConfig`) será estendido para incluir os novos roles.

## Observação sobre herança

`ROLE_ADMIN` herda todos os acessos de `ROLE_ATENDENTE` e `ROLE_TECNICO` neste sistema.
`ROLE_DEV` herda tudo de `ROLE_ADMIN` (comportamento do template).
