# 03 — Fluxo de Status das OS

## Estados e transições válidas

```
                       ┌─────────────────────────────────────────────────┐
                       │                   CANCELADO                     │
                       └─────────────────────────────────────────────────┘
                         ↑ (qualquer estado pode cancelar, exceto ENTREGUE)
                         │
RECEBIDO ──→ EM_ANALISE ──→ AGUARDANDO_APROVACAO ──┬──→ EM_MANUTENCAO ──→ PRONTO ──→ ENTREGUE
                                                   │         ↑↓
                                                   │   AGUARDANDO_PECA
                                                   │
                                                   └──→ CANCELADO (orçamento recusado)
```

## Tabela de transições

| De | Para | Quem pode | Trigger |
|----|------|-----------|---------|
| — | `RECEBIDO` | ATENDENTE, ADMIN | Abertura da OS |
| `RECEBIDO` | `EM_ANALISE` | TECNICO, ADMIN | Técnico assume a OS |
| `RECEBIDO` | `CANCELADO` | ATENDENTE, ADMIN | Recusa no recebimento |
| `EM_ANALISE` | `AGUARDANDO_APROVACAO` | TECNICO, ADMIN | Orçamento definido |
| `EM_ANALISE` | `CANCELADO` | TECNICO, ATENDENTE, ADMIN | Inviável de consertar |
| `AGUARDANDO_APROVACAO` | `EM_MANUTENCAO` | ATENDENTE, ADMIN | Orçamento aprovado pelo cliente |
| `AGUARDANDO_APROVACAO` | `CANCELADO` | ATENDENTE, ADMIN | Orçamento recusado pelo cliente |
| `EM_MANUTENCAO` | `AGUARDANDO_PECA` | TECNICO, ADMIN | Peça necessária não disponível |
| `EM_MANUTENCAO` | `PRONTO` | TECNICO, ADMIN | Reparo concluído |
| `EM_MANUTENCAO` | `CANCELADO` | TECNICO, ADMIN | Problema irresolvível |
| `AGUARDANDO_PECA` | `EM_MANUTENCAO` | TECNICO, ADMIN | Peça chegou, reparo retomado |
| `AGUARDANDO_PECA` | `CANCELADO` | TECNICO, ATENDENTE, ADMIN | Peça indisponível |
| `PRONTO` | `ENTREGUE` | ATENDENTE, ADMIN | Instrumento retirado pelo cliente |
| `PRONTO` | `EM_MANUTENCAO` | TECNICO, ADMIN | Problema reidentificado |

## Estados finais

`ENTREGUE` e `CANCELADO` são estados finais — sem transições saindo deles.

## Regras de negócio

- Toda transição gera um registro em `historico_os` com usuário, status anterior/novo e timestamp.
- `ENTREGUE` preenche `data_entrega = now()` na OS.
- `CANCELADO` via recusa de orçamento preenche automaticamente `observacoes` com "Orçamento recusado".
- A validação de transição acontece no domínio: `StatusOS.transicaoValida(de, para)` → lança `TransicaoStatusInvalidaException` se inválida.

## Representação Java

```java
static boolean transicaoValida(StatusOS de, StatusOS para) {
    return switch (de) {
        case RECEBIDO             -> para == EM_ANALISE || para == CANCELADO;
        case EM_ANALISE           -> para == AGUARDANDO_APROVACAO || para == CANCELADO;
        case AGUARDANDO_APROVACAO -> para == EM_MANUTENCAO || para == CANCELADO;
        case EM_MANUTENCAO        -> para == AGUARDANDO_PECA || para == PRONTO || para == CANCELADO;
        case AGUARDANDO_PECA      -> para == EM_MANUTENCAO || para == CANCELADO;
        case PRONTO               -> para == ENTREGUE || para == EM_MANUTENCAO;
        case ENTREGUE, CANCELADO  -> false;
    };
}
```
