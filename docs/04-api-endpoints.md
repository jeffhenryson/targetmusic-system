# 04 — Endpoints da API — Target Music

Endpoints de negócio (auth/segurança estão em `docs/api-reference.md` herdado do template).

Base URL: `http://localhost:8090`

---

## Clientes

| Método | Path | Permissão | Descrição |
|--------|------|-----------|-----------|
| `POST` | `/clientes` | `CLIENTE_CREATE` | Cadastra cliente |
| `GET` | `/clientes` | `CLIENTE_READ` | Lista clientes (paginado, busca por nome/email/telefone) |
| `GET` | `/clientes/{id}` | `CLIENTE_READ` | Busca cliente por ID |
| `PUT` | `/clientes/{id}` | `CLIENTE_UPDATE` | Atualiza cliente |
| `DELETE` | `/clientes/{id}` | `CLIENTE_DELETE` | Remove cliente |
| `GET` | `/clientes/{id}/instrumentos` | `INSTRUMENTO_READ` | Lista instrumentos do cliente |
| `GET` | `/clientes/{id}/os` | `OS_READ` | Lista OS do cliente |

### POST /clientes — request

```json
{
  "nome": "João Silva",
  "telefone": "11999999999",
  "email": "joao@email.com",
  "cpf": "123.456.789-00",
  "endereco": "Rua das Flores, 123",
  "observacoes": "cliente VIP"
}
```

### GET /clientes — query params

| Param | Tipo | Descrição |
|-------|------|-----------|
| `search` | string | Busca parcial em nome, email ou telefone |
| `page` | int | Página (default 0) |
| `size` | int | Tamanho (default 20) |

---

## Instrumentos

| Método | Path | Permissão | Descrição |
|--------|------|-----------|-----------|
| `POST` | `/instrumentos` | `INSTRUMENTO_CREATE` | Cadastra instrumento |
| `GET` | `/instrumentos/{id}` | `INSTRUMENTO_READ` | Busca instrumento por ID |
| `PUT` | `/instrumentos/{id}` | `INSTRUMENTO_UPDATE` | Atualiza instrumento |
| `DELETE` | `/instrumentos/{id}` | `INSTRUMENTO_DELETE` | Remove instrumento |

### POST /instrumentos — request

```json
{
  "clienteId": 1,
  "tipo": "GUITARRA",
  "marca": "Fender",
  "modelo": "Stratocaster",
  "numeroDeSerie": "MX21234567",
  "cor": "Sunburst",
  "descricao": "Pequena trinca no braço"
}
```

---

## Ordens de Serviço

| Método | Path | Permissão | Descrição |
|--------|------|-----------|-----------|
| `POST` | `/os` | `OS_CREATE` | Abre nova OS |
| `GET` | `/os` | `OS_READ` | Lista OS (paginado, filtros) |
| `GET` | `/os/{id}` | `OS_READ` | Busca OS por ID |
| `GET` | `/os/numero/{numero}` | `OS_READ` | Busca OS por número (ex: OS-2026-0001) |
| `PUT` | `/os/{id}` | `OS_UPDATE` | Atualiza campos editáveis (laudo, observações, prazo) |
| `PATCH` | `/os/{id}/tecnico` | `OS_ASSIGN_TECNICO` | Atribui técnico |
| `PATCH` | `/os/{id}/orcamento` | `OS_ORCAMENTO` | Define orçamento → status AGUARDANDO_APROVACAO |
| `PATCH` | `/os/{id}/orcamento/aprovar` | `OS_ORCAMENTO_APPROVE` | Aprova orçamento → EM_MANUTENCAO |
| `PATCH` | `/os/{id}/orcamento/recusar` | `OS_ORCAMENTO_APPROVE` | Recusa orçamento → CANCELADO |
| `PATCH` | `/os/{id}/status` | `OS_STATUS` | Transição de status genérica |
| `PATCH` | `/os/{id}/entrega` | `OS_ENTREGA` | Registra entrega → ENTREGUE |
| `DELETE` | `/os/{id}` | `OS_DELETE` | Remove OS (apenas CANCELADO ou RECEBIDO) |
| `GET` | `/os/{id}/historico` | `OS_READ` | Histórico de mudanças de status |

### POST /os — request

```json
{
  "instrumentoId": 1,
  "clienteId": 1,
  "descricaoProblema": "Instrumento com som falhando no canal 1",
  "observacoes": "Cliente pediu urgência"
}
```

### GET /os — query params

| Param | Tipo | Descrição |
|-------|------|-----------|
| `status` | StatusOS | Filtra por status |
| `clienteId` | long | Filtra por cliente |
| `tecnicoUsername` | string | Filtra por técnico |
| `page` | int | Página (default 0) |
| `size` | int | Tamanho (default 20) |

### PATCH /os/{id}/orcamento — request

```json
{
  "valor": 250.00,
  "prazoEstimado": "2026-06-20"
}
```

### PATCH /os/{id}/status — request

```json
{
  "novoStatus": "EM_MANUTENCAO",
  "observacao": "Peça chegou, retomando reparo"
}
```

### PATCH /os/{id}/entrega — request

```json
{
  "valorFinal": 230.00
}
```

---

## Portal do cliente (ROLE_CLIENTE)

| Método | Path | Permissão | Descrição |
|--------|------|-----------|-----------|
| `GET` | `/minha-conta/os` | autenticado | Lista as próprias OS |
| `GET` | `/minha-conta/os/{id}` | autenticado | Detalhe da própria OS |
| `GET` | `/minha-conta/instrumentos` | autenticado | Lista os próprios instrumentos |

> O filtro por `clienteId` é aplicado no service a partir do `userId` autenticado.
> O ROLE_CLIENTE não tem permissões RBAC explícitas — o endpoint valida apenas autenticação + ownership.

---

## Respostas comuns

### OrdemDeServico response

```json
{
  "id": 1,
  "numero": "OS-2026-0001",
  "status": "EM_MANUTENCAO",
  "instrumento": {
    "id": 1,
    "tipo": "GUITARRA",
    "marca": "Fender",
    "modelo": "Stratocaster"
  },
  "cliente": {
    "id": 1,
    "nome": "João Silva",
    "telefone": "11999999999"
  },
  "atendenteUsername": "atendente1",
  "tecnicoUsername": "tecnico1",
  "descricaoProblema": "Som falhando no canal 1",
  "laudoTecnico": "Potenciômetro com oxidação",
  "valorOrcamento": 250.00,
  "valorFinal": null,
  "prazoEstimado": "2026-06-20",
  "dataRecebimento": "2026-06-10T09:00:00Z",
  "dataEntrega": null,
  "observacoes": null,
  "createdAt": "2026-06-10T09:00:00Z",
  "updatedAt": "2026-06-10T14:30:00Z"
}
```

### Erros de negócio

| HTTP | Código | Situação |
|------|--------|----------|
| 404 | `OS_NOT_FOUND` | OS não encontrada |
| 404 | `CLIENTE_NOT_FOUND` | Cliente não encontrado |
| 404 | `INSTRUMENTO_NOT_FOUND` | Instrumento não encontrado |
| 422 | `TRANSICAO_STATUS_INVALIDA` | Tentativa de transição não permitida |
| 422 | `OS_NAO_PODE_SER_REMOVIDA` | Tentativa de remover OS em status não permitido |
| 409 | `CLIENTE_TEM_OS_EM_ABERTO` | Tentativa de remover cliente com OS ativa |
| 409 | `INSTRUMENTO_TEM_OS_EM_ABERTO` | Tentativa de remover instrumento com OS ativa |
