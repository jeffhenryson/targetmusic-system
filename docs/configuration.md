# Configuração

## Properties por categoria

### JWT

| Property | Env var | Padrão (dev) | Descrição |
|----------|---------|--------------|-----------|
| `jwt.secret` | `JWT_SECRET` | `dev-secret-...` | Chave HS256, base64 ou UTF-8. Mínimo 44 chars em prod (256 bits) |
| `jwt.access-ttl-minutes` | `JWT_ACCESS_TTL_MINUTES` | `15` | TTL do access token (JWT) em minutos |
| `jwt.refresh-ttl-days` | `JWT_REFRESH_TTL_DAYS` | `7` | TTL do refresh token em dias |
| `jwt.issuer` | `JWT_ISSUER` | `security-spring` | Claim `iss` do JWT — alterar em prod |
| `jwt.audience` | `JWT_AUDIENCE` | `api` | Claim `aud` do JWT — alterar em prod |

> `ProdStartupValidator` recusa boot se `jwt.issuer=security-spring` ou `jwt.audience=api` (valores default de dev).

---

### Cookie de Refresh

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `cookie.secure` | `COOKIE_SECURE` | `false` (dev), `true` (prod recomendado) | Se `true`, cookie marcado como `Secure` (HTTPS only) |

---

### OAuth2 — Google

| Property | Env var | Descrição |
|----------|---------|-----------|
| `oauth2.google.client-id` | `GOOGLE_CLIENT_ID` | Client ID do projeto no Google Console. Obrigatório para validar tokens do frontend |

---

### Email Verification

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `email.verification.ttl-minutes` | `EMAIL_VERIFICATION_TTL_MINUTES` | `15` | Validade do código de verificação |
| `email.verification.resend-cooldown-seconds` | `EMAIL_RESEND_COOLDOWN_SECONDS` | `60` | Intervalo mínimo entre reenvios |
| `email.verification.subject` | `EMAIL_VERIFICATION_SUBJECT` | `Código de confirmação de cadastro` | Assunto do email de verificação |

---

### Password Reset

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `password-reset.ttl-minutes` | `PASSWORD_RESET_TTL_MINUTES` | `15` | Validade do token de reset |
| `password-reset.frontend-url` | `PASSWORD_RESET_FRONTEND_URL` | `http://localhost:3000/reset-password` | Base URL do frontend para montar o link enviado por email |
| `password-reset.cleanup.cron` | — | `0 15 3 * * *` | Cron do scheduler de limpeza de tokens expirados |

---

### 2FA / TOTP

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `totp.encryption.key` | `TOTP_ENCRYPTION_KEY` | `AAAA...` (dev) | Chave AES-256 para cifrar o secret TOTP. Mínimo 32 chars em prod |
| `totp.app-name` | `TOTP_APP_NAME` | `security-spring` | Nome exibido no app autenticador (campo `issuer` do `otpauth://` URI) |
| `totp.pending-setup.ttl-hours` | — | `24` | Horas até remover setup de 2FA não confirmado |
| `totp.pending-setup.cleanup.cron` | — | `0 45 3 * * *` | Cron do scheduler de limpeza de setups pendentes |
| `totp.challenge.cleanup.cron` | — | `0 30 3 * * *` | Cron do scheduler de limpeza de challenge tokens expirados |

---

### Bloqueio de conta

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `auth.lockout.max-attempts` | `AUTH_LOCKOUT_MAX_ATTEMPTS` | `5` | Falhas de login antes de bloquear |
| `auth.lockout.duration-minutes` | `AUTH_LOCKOUT_DURATION_MINUTES` | `15` | Duração do bloqueio |
| `auth.max-sessions-per-user` | `AUTH_MAX_SESSIONS_PER_USER` | `5` | Máximo de sessões simultâneas por usuário (0 = sem limite) |
| `auth.registration.default-roles` | `AUTH_REGISTRATION_DEFAULT_ROLES` | vazio | Roles atribuídas automaticamente no auto-registro (`ROLE_USER,ROLE_X`). Vazio = mínimo privilégio |

---

### Rate Limiting de Login

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `rate.limit.login.window-seconds` | `LOGIN_RATE_WINDOW_SECONDS` | `60` | Janela de tempo do sliding window |
| `rate.limit.login.max-requests` | `LOGIN_RATE_MAX_REQUESTS` | `10` | Máximo de tentativas por IP na janela |

---

### Avatar

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `avatar.storage.type` | `AVATAR_STORAGE_TYPE` | `local` | `local` = sistema de arquivos; `s3` = Amazon S3 |
| `avatar.storage.dir` | `AVATAR_STORAGE_DIR` | `./uploads/avatars` | Diretório local de armazenamento (usado quando `type=local`) |
| `avatar.base-url` | `AVATAR_BASE_URL` | `http://localhost:8080` | Base URL para montar a `avatarUrl` retornada pela API |
| `avatar.max-size-bytes` | `AVATAR_MAX_SIZE_BYTES` | `2097152` (2 MB) | Tamanho máximo do arquivo em bytes |
| `avatar.s3.bucket` | `AVATAR_S3_BUCKET` | — | Bucket S3 (obrigatório quando `type=s3`) |
| `avatar.s3.region` | `AVATAR_S3_REGION` | `us-east-1` | Região AWS |
| `avatar.s3.public-url-base` | `AVATAR_S3_PUBLIC_URL_BASE` | — | URL base pública do S3/CDN para montar links de redirect |
| `avatar.s3.access-key` | `AVATAR_S3_ACCESS_KEY` | — | Access key AWS (opcional — usar IAM role em ECS/EC2) |
| `avatar.s3.secret-key` | `AVATAR_S3_SECRET_KEY` | — | Secret key AWS (opcional — usar IAM role em ECS/EC2) |

Limite de upload no servidor (antes do controller):
```
spring.servlet.multipart.max-file-size=3MB    # MULTIPART_MAX_FILE_SIZE
spring.servlet.multipart.max-request-size=3MB # MULTIPART_MAX_REQUEST_SIZE
```
O limite real do conteúdo (2 MB) é validado no service; o limite do servidor (3 MB) absorve o overhead do boundary multipart.

---

### CORS

| Property | Env var | Padrão (dev) | Padrão (hml/prod) |
|----------|---------|--------------|-------------------|
| `cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `*` | URL do frontend (obrigatório em prod) |
| `cors.allowed-methods` | `CORS_ALLOWED_METHODS` | `GET,POST,PUT,DELETE,OPTIONS,PATCH` | idem |
| `cors.allowed-headers` | `CORS_ALLOWED_HEADERS` | `*` | `Authorization,Content-Type` |
| `cors.exposed-headers` | — | `X-Trace-Id` | Headers de resposta visíveis ao JavaScript |
| `cors.allow-credentials` | `CORS_ALLOW_CREDENTIALS` | `false` | `true` — obrigatório para cookies HttpOnly |

> `PATCH` é necessário para `PATCH /users/me` e `PATCH /users/{id}`.  
> `allow-credentials=true` requer `allowed-origins` diferente de `*` — o browser rejeita a combinação.

---

### Segurança HTTP

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `security.content-security-policy` | — | vazio | Diretiva CSP adicionada ao header `Content-Security-Policy`. Vazio = header não enviado (útil em dev com Swagger) |

---

### Scheduler

| Property | Padrão | Descrição |
|----------|--------|-----------|
| `refresh-token.cleanup.cron` | `0 0 3 * * *` | Cleanup de refresh tokens expirados/revogados |
| `password-reset.cleanup.cron` | `0 15 3 * * *` | Cleanup de tokens de reset expirados |
| `email-verification.cleanup.cron` | `0 30 3 * * *` | Cleanup de códigos de verificação expirados |
| `totp.challenge.cleanup.cron` | `0 30 3 * * *` | Cleanup de challenge tokens TOTP expirados |
| `totp.pending-setup.cleanup.cron` | `0 45 3 * * *` | Cleanup de setups TOTP não confirmados |
| `audit.cleanup.cron` | `0 45 3 * * *` | Cleanup de audit logs antigos |
| `audit.retention-days` | `365` | Dias de retenção de audit logs |

---

### Seed (apenas `@Profile("dev")`)

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `seed.admin.password` | `SEED_ADMIN_PASSWORD` | `Admin@dev1` | Senha do usuário admin de teste |
| `seed.user.password` | `SEED_USER_PASSWORD` | `User@dev1` | Senha do usuário user de teste |

---

### Email (hml/prod — ResendEmailAdapter)

| Property | Env var | Descrição |
|----------|---------|-----------|
| `resend.api-key` | `RESEND_API_KEY` | API key do Resend.com |
| `resend.from` | `RESEND_FROM` | Endereço de envio (ex: `noreply@meuapp.com`) |

---

### HikariCP (pool de conexões)

| Property | Env var | Padrão | Descrição |
|----------|---------|--------|-----------|
| `spring.datasource.hikari.maximum-pool-size` | `HIKARI_MAX_POOL_SIZE` | `10` | Tamanho máximo do pool |
| `spring.datasource.hikari.minimum-idle` | `HIKARI_MIN_IDLE` | `2` | Conexões idle mínimas |
| `spring.datasource.hikari.connection-timeout` | `HIKARI_CONNECTION_TIMEOUT` | `30000` ms | Timeout para obter conexão |
| `spring.datasource.hikari.idle-timeout` | `HIKARI_IDLE_TIMEOUT` | `600000` ms | Timeout de conexão ociosa |
| `spring.datasource.hikari.max-lifetime` | `HIKARI_MAX_LIFETIME` | `1800000` ms | Lifetime máximo de conexão |

---

## Perfis (Spring Profiles)

### `dev`
- Banco H2 in-memory (Flyway desabilitado, DDL `create-drop`)
- `LoggingEmailAdapter` — mensagens logadas no console e retidas em memória para testes
- `InMemoryLoginRateLimiterAdapter` / `InMemoryLoginAttemptAdapter`
- `InMemoryTokenBlocklistAdapter`
- `SeedConfig` cria usuários, roles e permissões de teste
- H2 console habilitado em `/h2-console`
- `DevModeWarningConfig` loga aviso de modo dev no startup
- Avatar: armazenamento local em `./uploads/avatars`

### `hml`
- PostgreSQL + Flyway
- `ResendEmailAdapter`
- Redis (rate limit, blocklist)
- `HmlStartupValidator` — verifica JWT secret, DB_PASSWORD, REDIS_PASSWORD, Resend, CORS, GOOGLE_CLIENT_ID
- Swagger habilitado via `SWAGGER_ENABLED=true` (desabilitado por padrão)
- Sem seed automático

### `prod`
- PostgreSQL + Flyway
- `ResendEmailAdapter`
- Redis (rate limit, blocklist)
- `ProdStartupValidator` — verifica: `jwt.secret` (≥44 chars), CORS não pode ser `*`, DB não pode ser localhost/H2, `jwt.issuer` e `jwt.audience` não podem ser defaults, `totp.encryption.key` (≥32 chars), `avatar.base-url` não pode ser localhost, `GOOGLE_CLIENT_ID` obrigatório
- Sem seed automático

### `shell`
- Ativa o CLI administrativo (`AdminCliRunner`)
- `spring.main.web-application-type=none` — sem servidor HTTP
- Usar combinado com outro perfil: `dev,shell` ou `prod,shell`

---

## Startup Validators

### HmlStartupValidator (`@Profile("hml")`)

Executa no startup (`ApplicationReadyEvent`). Verifica:
- `jwt.secret` definida
- `DB_PASSWORD` configurado
- `REDIS_PASSWORD` configurado
- `resend.api-key` definida
- `resend.from` definida
- `cors.allowed-origins` não pode ser `*`
- `GOOGLE_CLIENT_ID` definido

### ProdStartupValidator (`@Profile("prod")`)

Executa no startup. Verifica:

| Variável | Property | Regra |
|----------|----------|-------|
| `JWT_SECRET` | `jwt.secret` | Mínimo 44 chars (256 bits em Base64) |
| `CORS_ALLOWED_ORIGINS` | `cors.allowed-origins` | Não pode ser `*` |
| `DB_URL` | `spring.datasource.url` | Não pode apontar para localhost ou H2 |
| `RESEND_API_KEY` | `resend.api-key` | Chave real obrigatória |
| `RESEND_FROM` | `resend.from` | Domínio real obrigatório |
| `JWT_ISSUER` | `jwt.issuer` | Não pode ser `security-spring` (default) |
| `JWT_AUDIENCE` | `jwt.audience` | Não pode ser `api` (default) |
| `TOTP_ENCRYPTION_KEY` | `totp.encryption.key` | Mínimo 32 chars |
| `AVATAR_BASE_URL` | `avatar.base-url` | Não pode apontar para localhost |
| `GOOGLE_CLIENT_ID` | `oauth2.google.client-id` | Obrigatório para validar tokens Google |

---

## Beans de configuração (`infra/config/`)

### CoreBeanConfig

Instancia e injeta todos os services:
```
AuthService, OAuthLoginService, TotpService, AvatarService,
UserService, RoleService, PermissionService, AuditLogsService, StatsService
```

Habilita `@EnableScheduling` e `@EnableCaching`.

### ConverterBeanConfig

Instancia `UserDTOConverter`, `RoleDTOConverter`, `PermissionDTOConverter`.

### AsyncConfig

Configura executor assíncrono (`ThreadPoolTaskExecutor`) para o `EmailPort`.  
O HTTP response retorna imediatamente sem esperar a entrega do email.

### S3StorageConfig

Ativado apenas quando `avatar.storage.type=s3`.  
Cria `S3Client` (com credenciais via env ou IAM role) e registra `S3AvatarStorageAdapter` como bean `AvatarStoragePort`.

### OAuthConfig

`NimbusJwtDecoder` configurado com JWKS do Google (`https://www.googleapis.com/oauth2/v3/certs`), issuer `https://accounts.google.com` e audience = `oauth2.google.client-id`.

### OpenApiConfig

Swagger UI em `/swagger-ui/index.html`  
API docs em `/v3/api-docs`  
Bearer auth configurado como security scheme global.  
Desabilitado por padrão em hml/prod (`springdoc.swagger-ui.enabled=false`).

### ShedLockConfig

Lock provider usando JDBC (tabela `shedlock` criada automaticamente).  
Todos os schedulers usam `@SchedulerLock` para execução única em múltiplas instâncias.

---

## Cache

| Perfil | Implementação | Cache names |
|--------|--------------|-------------|
| `dev` | Caffeine (local) | `userDetails` |
| `hml`/`prod` | Redis (distribuído entre instâncias) | `userDetails` |

TTL padrão do cache `userDetails`: **60 segundos**.
- Dev: `spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=60s`
- hml/prod: `spring.cache.redis.time-to-live=60s`

Eviction chamada por `UserCachePort.evict(username)` em toda mutação de usuário.
