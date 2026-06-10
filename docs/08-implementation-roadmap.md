# 08 — Roadmap de Implementação

Backend REST API — sem frontend. Toda feature é entregue com testes (unit + integração).

Sprints de implementação do negócio Target Music sobre a base do template de segurança.
Migrations partem de V44 (template encerrou em V43).

## Estratégia de testes

Cada sprint entrega:
- **Testes unitários** dos services (`*ServiceTest`) com Mockito — validam regras de negócio e domínio.
- **Testes de integração** dos controllers (`*ControllerIT`) com `@SpringBootTest` + MockMvc — validam autenticação, RBAC e formato de resposta.
- **Testes Testcontainers** para casos que exigem banco real (transações, concorrência, constraints).

---

## Sprint A — Fundação de domínio (V44–V47)

**Objetivo:** banco + models do core prontos.

| # | O que | Migration |
|---|-------|-----------|
| A1 | Tabela `clientes` | V44 |
| A2 | Tabela `instrumentos` | V45 |
| A3 | Tabela `ordens_de_servico` + sequência de número | V46 |
| A4 | Tabela `historico_os` | V47 |

**Entregáveis de código:**
- `TipoInstrumento` (enum)
- `StatusOS` (enum) com `transicaoValida()`
- `Cliente`, `Instrumento`, `OrdemDeServico`, `HistoricoOS` (domain models)
- `TransicaoStatusInvalidaException`, `ClienteNotFoundException`, etc.

**Testes:** `StatusOSTest` cobrindo todas as transições válidas e inválidas.

---

## Sprint B — Clients + Instrumentos (CRUD)

**Objetivo:** CRUD completo de clientes e instrumentos funcionando.

| Layer | O que implementar |
|-------|-------------------|
| Port OUT | `ClienteRepository`, `InstrumentoRepository` |
| Port IN | `ClienteUseCase`, `InstrumentoUseCase` |
| Adapter OUT | `ClienteRepositoryImpl` (JPA), `InstrumentoRepositoryImpl` (JPA) |
| Adapter IN | `ClienteController`, `InstrumentoController` |
| DTOs | `ClienteRequest`, `ClienteResponse`, `InstrumentoRequest`, `InstrumentoResponse` |
| Testes unit | `ClienteServiceTest`, `InstrumentoServiceTest` (regras de negócio, bloqueio de deleção) |
| Testes IT | `ClienteControllerIT`, `InstrumentoControllerIT` (RBAC, 401/403, paginação) |

---

## Sprint C — Ordens de Serviço (abertura + fluxo básico)

**Objetivo:** abrir OS, atribuir técnico, mudar status simples.

| Layer | O que implementar |
|-------|-------------------|
| Port OUT | `OrdemDeServicoRepository`, `HistoricoOSRepository`, `OSNumeroSequencePort` |
| Port IN | `OrdemDeServicoUseCase` (métodos: abrir, atribuirTecnico, atualizarStatus, buscarHistorico) |
| Adapter OUT | `OrdemDeServicoRepositoryImpl`, `HistoricoOSRepositoryImpl`, `OSNumeroSequenceAdapter` |
| Adapter IN | `OrdemDeServicoController` (POST /os, GET /os, GET /os/{id}, PATCH /os/{id}/tecnico, PATCH /os/{id}/status) |
| Testes unit | `OrdemDeServicoServiceTest` (todas as transições válidas e inválidas) |
| Testes IT | `OrdemDeServicoControllerIT` (RBAC por role, geração de número, histórico) |

---

## Sprint D — Orçamento e entrega

**Objetivo:** fluxo completo de orçamento (aprovar/recusar) e entrega.

| O que | Endpoint |
|-------|----------|
| Técnico define orçamento | `PATCH /os/{id}/orcamento` |
| Atendente aprova orçamento | `PATCH /os/{id}/orcamento/aprovar` |
| Atendente recusa orçamento | `PATCH /os/{id}/orcamento/recusar` |
| Atendente registra entrega | `PATCH /os/{id}/entrega` |

---

## Sprint E — Roles e permissões de negócio

**Objetivo:** RBAC do negócio funcionando (ROLE_ATENDENTE, ROLE_TECNICO, ROLE_CLIENTE).

| # | O que | Migration |
|---|-------|-----------|
| E1 | Permissões de negócio (CLIENTE_*, INSTRUMENTO_*, OS_*) | V48 |
| E2 | Roles ROLE_ATENDENTE, ROLE_TECNICO, ROLE_CLIENTE + atribuição de permissões | V49 |
| E3 | `@PreAuthorize` nos controllers | — |
| E4 | Bootstrap dos roles no `DevRoleBootstrapConfig` | — |

---

## Sprint F — Endpoints do cliente (ROLE_CLIENTE)

**Objetivo:** ROLE_CLIENTE acessa suas próprias OS via `/minha-conta/**` — sem frontend, validado por testes de integração com usuário `ROLE_CLIENTE` autenticado.

| O que | Descrição |
|-------|-----------|
| `GET /minha-conta/os` | Lista OS do cliente autenticado (filtro por ownership no service) |
| `GET /minha-conta/os/{id}` | Detalhe com validação de ownership (404 se não pertencer ao cliente) |
| `GET /minha-conta/instrumentos` | Instrumentos do cliente autenticado |
| Vinculação | `vincularUsuario(clienteId, userId)` no cadastro |

**Testes IT:** `MinhaContaControllerIT` — acesso próprio (200), acesso alheio (404), sem autenticação (401).

---

## Backlog (pós-MVP)

| Feature | Prioridade |
|---------|------------|
| Notificações por email de OS (pronto/entregue) | Alta |
| Relatórios (OS por período, por técnico, faturamento) | Alta |
| Dashboard com contadores por status | Média |
| Histórico de acessos do cliente | Baixa |
| Aprovação de orçamento online (link por email) | Baixa |
| Integração WhatsApp | Baixa |

---

## Estado atual

| Sprint | Status |
|--------|--------|
| Template segurança (V1–V43) | ✅ Completo |
| Sprint A — Fundação domínio | Pendente |
| Sprint B — Clientes + Instrumentos | Pendente |
| Sprint C — OS básico | Pendente |
| Sprint D — Orçamento e entrega | Pendente |
| Sprint E — RBAC negócio | Pendente |
| Sprint F — Portal cliente | Pendente |
