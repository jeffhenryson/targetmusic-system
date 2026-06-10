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

## ✅ Sprint A — Fundação de domínio (V44–V50) — COMPLETO

**Objetivo:** banco + models do core prontos. Decisões de design:
- Número de OS: sequência global (`OS-YYYY-NNNN`, nunca reinicia)
- Técnicos por OS: múltiplos (tabela `os_tecnicos`)
- Peças: estoque completo (`pecas` + `os_pecas`)

| # | O que | Migration | Status |
|---|-------|-----------|--------|
| A1 | Tabela `clientes` | V44 | ✅ |
| A2 | Tabela `instrumentos` | V45 | ✅ |
| A3 | Tabela `ordens_de_servico` + sequence global | V46 | ✅ |
| A4 | Tabela `os_tecnicos` (junction) | V47 | ✅ |
| A5 | Tabela `historico_os` | V48 | ✅ |
| A6 | Tabela `pecas` (estoque) | V49 | ✅ |
| A7 | Tabela `os_pecas` (peças utilizadas por OS) | V50 | ✅ |

**Entregáveis de código:** ✅
- `TipoInstrumento`, `StatusOS` (enums)
- `Cliente`, `Instrumento`, `OrdemDeServico`, `HistoricoOS`, `Peca`, `OSPeca` (domain models)
- `TransicaoStatusInvalidaException`, `ClienteNotFoundException`, `InstrumentoNotFoundException`,
  `OrdemDeServicoNotFoundException`, `ClienteTemOSEmAbertoException`,
  `InstrumentoTemOSEmAbertoException`, `EstoqueInsuficienteException`, `PecaNotFoundException`

**Testes (TDD):** ✅ 48 testes, 0 falhas
- `StatusOSTest` — todas as 14 transições (válidas e inválidas)
- `OrdemDeServicoTest` — `abrir()`, `mudarStatus()`, `adicionarTecnico()`, `registrarEntrega()`
- `PecaTest` — `darSaida()` bloqueia se estoque < quantidade, `darEntrada()`, `desativar()`

**Commit:** `43f9d78`

---

## ✅ Sprint B — Clientes + Instrumentos (CRUD) — COMPLETO

**Objetivo:** CRUD completo de clientes e instrumentos funcionando.

| Layer | O que implementar | Status |
|-------|-------------------|--------|
| Port OUT | `ClienteRepository`, `InstrumentoRepository` | ✅ |
| Port IN | `ClienteUseCase`, `InstrumentoUseCase` | ✅ |
| Service | `ClienteService`, `InstrumentoService` | ✅ |
| Adapter OUT | `ClienteRepositoryImpl` (JPA), `InstrumentoRepositoryImpl` (JPA) | ✅ |
| Entities | `ClienteEntity`, `InstrumentoEntity` | ✅ |
| Converters | `ClienteEntityConverter`, `InstrumentoEntityConverter` (domain ↔ entity) | ✅ |
| Adapter IN | `ClienteController`, `InstrumentoController` | ✅ |
| DTOs | `ClienteRequest`, `ClienteResponse`, `InstrumentoRequest`, `InstrumentoResponse` | ✅ |
| Infra | `GlobalExceptionHandler` + `CoreBeanConfig` + `ConverterBeanConfig` atualizados | ✅ |
| Testes unit | `ClienteServiceTest` (11), `InstrumentoServiceTest` (11) | ✅ |
| Testes controller | `ClienteControllerTest` (11), `InstrumentoControllerTest` (10) | ✅ |

**Endpoints entregues:**
- `POST /clientes` · `GET /clientes` · `GET /clientes/{id}` · `PUT /clientes/{id}` · `DELETE /clientes/{id}`
- `GET /clientes/{id}/instrumentos`
- `POST /instrumentos` · `GET /instrumentos/{id}` · `PUT /instrumentos/{id}` · `DELETE /instrumentos/{id}`

**Regras de negócio implementadas:**
- `DELETE /clientes/{id}` → 409 se cliente tiver OS em aberto
- `DELETE /instrumentos/{id}` → 409 se instrumento tiver OS em aberto
- `POST /instrumentos` → 404 se `clienteId` não existir
- `hasOpenOS` via native SQL (tabela `ordens_de_servico` ainda sem JPA entity)

**Testes (TDD):** ✅ 43 novos testes, total acumulado: 714, 0 falhas

**Commits:** `c82d5ee` → `63dbf71` → `4f96a2e` → `c066d8a` → `bb955a0` → `33d821b` → `c238f90`

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

| Sprint | Status | Testes |
|--------|--------|--------|
| Template segurança (V1–V43) | ✅ Completo | 671 |
| Sprint A — Fundação domínio | ✅ Completo | +48 |
| Sprint B — Clientes + Instrumentos | ✅ Completo | +43 → **714 total** |
| Sprint C — OS básico | 🔲 Pendente | — |
| Sprint D — Orçamento e entrega | 🔲 Pendente | — |
| Sprint E — RBAC negócio | 🔲 Pendente | — |
| Sprint F — Portal cliente | 🔲 Pendente | — |
