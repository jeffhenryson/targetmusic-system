# CLI Administrativo

O CLI administrativo do **security-spring** é ativado pelo perfil `shell` e permite executar
operações de manutenção diretamente pela linha de comando — sem precisar de uma API REST
autenticada ou acesso ao banco de dados.

Implementado como um `ApplicationRunner` do Spring Boot, o CLI não requer dependências externas:
usa apenas as mesmas portas de domínio da aplicação.

---

## Pré-requisitos

| Requisito | Dev (`--spring.profiles.active=dev,shell`) | Hml/Prod |
|---|---|---|
| Banco de dados | H2 in-memory (automático) | PostgreSQL configurado via env |
| Redis | **Não necessário** (adaptadores InMemory ativos no dev) | Redis configurado via env |
| Variáveis de ambiente | Defaults de dev aplicados automaticamente | Ver `application-hml.properties` / `application-prod.properties` |

---

## Formas de execução

### JAR empacotado

```bash
java -jar target/security-spring-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=dev,shell \
  <comando> [--opcao=valor]
```

> Em **hml** ou **prod**, substitua `dev` pelo perfil correspondente e forneça as variáveis
> de ambiente necessárias (banco, Redis, JWT secret, etc.).

### Maven (ambiente dev)

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=dev,shell \
  "-Dspring-boot.run.arguments=<comando> [--opcao=valor]"
```

> As aspas externas são necessárias no shell quando o argumento contém espaços.

---

## Comandos

### `hash-password`

Gera o hash BCrypt de uma senha em texto claro. Útil para criar usuários manualmente
via SQL ou para verificar o hash antes de um `INSERT`.

```bash
java -jar app.jar --spring.profiles.active=dev,shell \
  hash-password --password=MinhaS3nh@1
```

**Saída:**
```
$2a$10$xK9...
```

---

### `create-admin`

Cria um novo usuário com `ROLE_ADMIN`. A conta é criada **ativa** imediatamente —
sem necessidade de verificação de email.

```bash
java -jar app.jar --spring.profiles.active=dev,shell \
  create-admin --username=alice --password=Admin@Seguro1
```

**Saída:**
```
✓ Administrador criado: alice (id=3)
```

| Opção | Obrigatória | Descrição |
|---|---|---|
| `--username` | ✅ | Nome de usuário único |
| `--password` | ✅ | Senha em texto claro (será hasheada internamente) |

> **Atenção:** a senha deve atender à política de complexidade da aplicação
> (mínimo 8 caracteres, letras maiúsculas/minúsculas, número e símbolo).

---

### `enable-user`

Ativa a conta de um usuário que está desabilitada.

```bash
java -jar app.jar --spring.profiles.active=dev,shell \
  enable-user --id=7
```

**Saída:**
```
✓ Conta ativada: alice
```

| Opção | Obrigatória | Descrição |
|---|---|---|
| `--id` | ✅ | ID numérico do usuário (inteiro) |

---

### `disable-user`

Desativa a conta de um usuário. O login será bloqueado até que a conta seja reativada.
As sessões existentes **não são revogadas automaticamente** — use `revoke-sessions`
em seguida se necessário.

```bash
java -jar app.jar --spring.profiles.active=dev,shell \
  disable-user --id=7
```

**Saída:**
```
✓ Conta desativada: alice
```

---

### `unlock-account`

Remove o bloqueio de login causado por excesso de tentativas com senha errada.
Se a conta não estiver bloqueada, informa sem errar.

```bash
java -jar app.jar --spring.profiles.active=dev,shell \
  unlock-account --username=alice
```

**Saída:**
```
✓ Bloqueio removido: alice
```
ou
```
ℹ Conta 'alice' não está bloqueada.
```

---

### `list-sessions`

Lista todas as sessões ativas (refresh tokens não expirados e não revogados) de um
usuário — com ID, data de criação, data de expiração, IP e User-Agent.

```bash
java -jar app.jar --spring.profiles.active=dev,shell \
  list-sessions --username=alice
```

**Saída:**
```
Sessões ativas de 'alice' (2):
  ID      Criada em            Expira em            IP               User-Agent
  ────────────────────────────────────────────────────────────────────────────────────────
  12      2026-05-27 10:00:00  2026-06-03 10:00:00  192.168.0.10     Mozilla/5.0 (Windows…
  15      2026-05-27 14:30:00  2026-06-03 14:30:00  10.0.0.5         curl/8.7.1
```

---

### `revoke-sessions`

Revoga **todas** as sessões ativas de um usuário — equivalente a forçar logout em
todos os dispositivos. O próximo uso de qualquer refresh token retornará 401.

```bash
java -jar app.jar --spring.profiles.active=dev,shell \
  revoke-sessions --username=alice
```

**Saída:**
```
✓ Todas as sessões revogadas para 'alice'.
```

---

### `help`

Exibe a lista de comandos disponíveis com exemplos de uso.

```bash
java -jar app.jar --spring.profiles.active=dev,shell help
```

Também é exibido automaticamente quando nenhum comando é informado.

---

## Tratamento de erros

| Situação | Comportamento |
|---|---|
| Opção obrigatória ausente | Mensagem `[ERRO] opção obrigatória ausente: --<opcao>` + uso do comando; exit code 1 |
| `--id` com valor não numérico | Mensagem `[ERRO] --id deve ser um número inteiro`; exit code 1 |
| Exceção do domínio (ex: usuário não encontrado) | Mensagem com nome da exceção e detalhe; exit code 1 |
| Sucesso | Mensagem de confirmação; exit code 0 |

---

## Exemplos de fluxo completo

### Criar o primeiro administrador em produção

```bash
# 1. Gere o hash para conferência (opcional)
java -jar app.jar --spring.profiles.active=prod,shell \
  hash-password --password="$ADMIN_PASSWORD"

# 2. Crie o admin
java -jar app.jar --spring.profiles.active=prod,shell \
  create-admin --username=admin --password="$ADMIN_PASSWORD"
```

### Bloquear e desbloquear usuário comprometido

```bash
# Desativa o acesso imediatamente
java -jar app.jar --spring.profiles.active=prod,shell disable-user --id=42

# Revoga todas as sessões ativas (logout forçado)
java -jar app.jar --spring.profiles.active=prod,shell revoke-sessions --username=alice

# Após investigação, reativa
java -jar app.jar --spring.profiles.active=prod,shell enable-user --id=42
```

---

## Arquitetura

O CLI é implementado em [AdminCliRunner.java](../src/main/java/com/securityspring/infra/cli/AdminCliRunner.java)
como um `@Component @Profile("shell")` que implementa `ApplicationRunner`.

O perfil `shell` (`application-shell.properties`) define apenas:
```properties
spring.main.web-application-type=none
```

O Spring Boot sobe o contexto completo da aplicação (JPA, segurança, serviços), executa
o `AdminCliRunner.run()` com os argumentos recebidos e encerra o processo naturalmente.
Nenhuma dependência extra é necessária além das já presentes na aplicação.
