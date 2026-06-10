# 00 — Visão Geral — Target Music

Sistema de gestão de oficina de conserto e manutenção de instrumentos musicais.

Backend REST API — sem frontend neste repositório. Toda funcionalidade é validada via testes automatizados.

## Propósito

Controlar o ciclo completo de uma Ordem de Serviço (OS): recebimento do instrumento,
diagnóstico, orçamento, execução do reparo e entrega ao cliente.

## Atores

| Ator | Descrição |
|------|-----------|
| **Atendente** | Recebe o instrumento, abre OS, emite orçamento ao cliente, registra entrega |
| **Técnico** | Diagnostica o problema, atualiza status técnico, registra laudo |
| **Admin** | Gestão completa do sistema, relatórios, configuração |
| **Cliente** | Portal de acompanhamento da própria OS (read-only) |
| **Dev** | Acesso elevado ao sistema operacional (herdado do template) |

## Fluxo resumido

```
Cliente traz instrumento
        ↓
  Atendente abre OS (RECEBIDO)
        ↓
  Técnico diagnostica (EM_ANALISE)
        ↓
  Orçamento enviado (AGUARDANDO_APROVACAO)
     ↓            ↓
  Aprovado     Recusado → CANCELADO
     ↓
  Execução (EM_MANUTENCAO)
     ↓  ↕ (aguarda peça)
  Reparo concluído (PRONTO)
     ↓
  Cliente retira (ENTREGUE)
```

## Stack

| Camada | Tecnologia |
|--------|------------|
| Backend | Spring Boot 4.0.6, Java 21 |
| Segurança | JWT + 2FA TOTP (herdado do template) |
| ORM | Spring Data JPA + Flyway |
| Banco dev | H2 in-memory |
| Banco hml/prod | PostgreSQL 16 |
| Cache/Rate limit | Caffeine (dev) / Redis (hml/prod) |
| Testes | JUnit 5 + Mockito + Testcontainers |
| Documentação | Springdoc OpenAPI (Swagger UI) |

## Portas

| Serviço | Porta |
|---------|-------|
| API | 8090 |
| Actuator | 8091 |
| PostgreSQL | 5434 |
| Redis | 6381 |
| Prometheus | 9091 |
| Grafana | 3001 |

## Documentação relacionada

- [01 — Ports](01-ports.md)
- [02 — Modelo de Domínio](02-domain-model.md)
- [03 — Fluxo de Status das OS](03-status-workflow.md)
- [04 — Endpoints da API](04-api-endpoints.md)
- [05 — Integrações Externas](05-integrations.md)
- [06 — Roles e Permissões](06-roles-permissions.md)
- [08 — Roadmap de Implementação](08-implementation-roadmap.md)
