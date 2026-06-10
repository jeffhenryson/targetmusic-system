# target-music-spring

Sistema de gestão da **Target Music** — oficina de conserto e manutenção de instrumentos musicais.

Backend REST API — sem frontend neste repositório.

## Stack

- Java 21 + Spring Boot 4 + Arquitetura Hexagonal
- PostgreSQL 16 + Flyway
- Spring Security 6 (JWT, 2FA, OAuth2 Google)
- Redis (cache, sessões, rate limiting)
- SSE para notificações em tempo real
- Prometheus + Grafana
- Springdoc OpenAPI (Swagger UI)
- JUnit 5 + Mockito + Testcontainers

## Como rodar

### Dev (H2 in-memory, sem Docker)

```bash
./mvnw spring-boot:run
```

API: `http://localhost:8090`  
Swagger: `http://localhost:8090/swagger-ui.html`  
H2 Console: `http://localhost:8090/h2-console`

### HML (PostgreSQL + Redis via Docker)

```bash
# Subir infraestrutura
docker compose up -d

# Subir aplicação
SPRING_PROFILES_ACTIVE=hml \
  ./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dserver.port=8090"
```

Grafana: `http://localhost:3001`  
Prometheus: `http://localhost:9091`

## Portas

| Serviço | Porta |
|---------|-------|
| API | 8090 |
| Actuator | 8091 |
| PostgreSQL | 5434 |
| Redis | 6381 |
| Prometheus | 9091 |
| Grafana | 3001 |

## Testes

```bash
./mvnw test
```

## Documentação

Ver pasta `docs/`:

- [00 — Visão Geral](docs/00-overview.md)
- [01 — Portas](docs/01-ports.md)
- [02 — Modelo de Domínio](docs/02-domain-model.md)
- [03 — Fluxo de Status das OS](docs/03-status-workflow.md)
- [04 — Endpoints da API](docs/04-api-endpoints.md)
- [05 — Integrações Externas](docs/05-integrations.md)
- [06 — Roles e Permissões](docs/06-roles-permissions.md)
- [08 — Roadmap de Implementação](docs/08-implementation-roadmap.md)
