# security-spring — Documentação

Template reutilizável de Spring Boot 4 com camada de segurança completa, arquitetura hexagonal e RBAC.

## Índice

| Arquivo | Conteúdo |
|---------|----------|
| [architecture.md](architecture.md) | Arquitetura hexagonal, estrutura de pacotes, convenções por camada |
| [domain-model.md](domain-model.md) | Modelos de domínio, ports (in/out), regras de negócio dos services |
| [api-reference.md](api-reference.md) | Todos os endpoints REST: métodos, paths, permissões, DTOs |
| [security.md](security.md) | JWT, refresh tokens, rate limiting, email verification, blocklist |
| [persistence.md](persistence.md) | Entidades JPA, schemas, repositórios, Flyway |
| [configuration.md](configuration.md) | Todas as properties, perfis (dev/hml/prod), startup validators |
| [flows.md](flows.md) | Fluxos end-to-end: login, refresh, registro, verificação de email |
| [testing.md](testing.md) | Estratégia de testes, categorias, como rodar, Testcontainers, helpers de teste |
| [roadmap.md](roadmap.md) | Funcionalidades planejadas e implementadas — status por tier |
| [15-plano-implementacao-backend.md](15-plano-implementacao-backend.md) | Sprints A–F de integração backend+frontend (2026-06-01) |
| [16-features-jun2026.md](16-features-jun2026.md) | **Planejamento ativo** — F1 visibilidade DEV, F2 timer, F3 Grafana, F4 feature flags |

## Stack resumida

| Camada | Tecnologia |
|--------|-----------|
| Framework | Spring Boot 4.0.6, Java 21 |
| Segurança | Spring Security + JWT (JJWT 0.12.6) |
| ORM | Spring Data JPA |
| Banco dev | H2 in-memory |
| Banco hml/prod | PostgreSQL + Flyway |
| Cache dev | Caffeine |
| Cache hml/prod | Redis |
| Rate limit/Blocklist hml/prod | Redis |
| Scheduling distribuído | ShedLock 5.16.0 |
| Email dev | LoggingEmailAdapter (console) |
| Email hml/prod | Resend.com API |
| Documentação API | SpringDoc OpenAPI 3 (Swagger) |
| Testes | JUnit 5 + Mockito + Testcontainers |