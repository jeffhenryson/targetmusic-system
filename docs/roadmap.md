# Roadmap de Funcionalidades — security-spring

> Documento de planejamento das próximas implementações.  
> Organizado por prioridade e complexidade de implementação.

---

## Estado atual do template

O projeto já cobre uma base sólida de segurança:

| Funcionalidade | Status |
|---|---|
| JWT + refresh token com rotação | ✅ |
| Rate limiting (Redis + in-memory) | ✅ |
| Bloqueio de conta por tentativas | ✅ |
| Verificação de email | ✅ |
| RBAC (roles + permissions) | ✅ |
| Gestão de sessões | ✅ |
| Limite de sessões simultâneas | ✅ |
| Auditoria de eventos | ✅ |
| Alertas de segurança por email | ✅ |
| Métricas Micrometer + Prometheus | ✅ |
| Política de senha | ✅ |
| Blocklist de tokens | ✅ |
| Limpeza agendada de tokens expirados | ✅ |

---

## Tier 1 — Alta prioridade (presentes em quase todo projeto)

### 1. Password Reset ("Esqueci minha senha") ✅ Implementado

**O que é:** fluxo para o usuário recuperar o acesso sem estar logado.

**Fluxo:**
```
POST /auth/forgot-password  { email }
  → gera token único (hash salvo no banco com TTL de 15 min)
  → envia email com link: /reset-password?token=...

POST /auth/reset-password  { token, newPassword }
  → valida token (existe, não expirado, não usado)
  → aplica PasswordPolicy
  → atualiza senha + marca token como usado
  → revoga todas as sessões ativas do usuário (segurança)
```

**Modelos novos:**
- `PasswordResetToken` em `core/domain/model/auth/`  
  — campos: `id`, `username`, `tokenHash`, `expiresAt`, `usedAt`

**Portas novas:**
- `PasswordResetTokenRepository` (out) — salvar, buscar, marcar como usado
- Reutiliza `EmailPort` que já existe

**Por que é prioridade máxima:** toda aplicação real precisa. O padrão é idêntico ao `EmailVerificationCode` já implementado — a curva de desenvolvimento é baixa.

**Complexidade:** ⭐⭐ Baixa

---

### 2. 2FA via TOTP (autenticação em dois fatores) ✅ Implementado

**O que é:** segundo fator de autenticação via app (Google Authenticator, Authy, 1Password). Baseado no padrão RFC 6238.

**Fluxo de configuração:**
```
POST /auth/2fa/setup
  → gera secret TOTP para o usuário
  → retorna: secret + URI para QR code (padrão otpauth://)
  → usuário escaneia no app e confirma com primeiro código

POST /auth/2fa/confirm  { code }
  → valida código TOTP
  → marca 2FA como ativo + gera backup codes

DELETE /auth/2fa/disable  { password, code }
  → desativa 2FA (exige senha + código atual por segurança)
```

**Fluxo de login modificado:**
```
POST /auth/login  { username, password }
  ├── sem 2FA → retorna TokenPair (comportamento atual)
  └── com 2FA → retorna { status: "PENDING_2FA", challengeToken: "..." }

POST /auth/2fa/challenge  { challengeToken, totpCode }
  → valida código TOTP
  → retorna TokenPair definitivo
```

**Modelos novos:**
- `TotpConfig` em `core/domain/model/auth/`  
  — campos: `id`, `username`, `secretEncrypted`, `enabled`, `confirmedAt`
- `TotpBackupCode` em `core/domain/model/auth/`  
  — campos: `id`, `username`, `codeHash`, `usedAt`

**Dependência Maven:**
```xml
<dependency>
  <groupId>dev.samstevens.totp</groupId>
  <artifactId>totp-spring-boot-starter</artifactId>
  <version>1.7.1</version>
</dependency>
```

**Detalhes importantes:**
- O `challengeToken` tem TTL curto (~5 min) e uso único — evita brute force no segundo fator
- O secret TOTP deve ser criptografado em repouso (AES) — não salvar em texto plano
- Backup codes: gerar 8 códigos de uso único para recuperação de emergência
- Ao usar um backup code, enviar email de alerta ao usuário

**Complexidade:** ⭐⭐⭐ Média

---

### 3. Email change confirmation (troca segura de email) ✅ Implementado

**O que é:** ao trocar o email, o novo endereço só passa a ser o definitivo após ser confirmado. Até lá, o email antigo continua ativo.

**Fluxo atual (problemático):**
```
PATCH /users/me  { newEmail }
  → troca email imediatamente + emailVerified = false
  ← se alguém roubou a conta, pode trocar o email antes do dono perceber
```

**Fluxo correto:**
```
PATCH /users/me  { newEmail, currentPassword }
  → salva pendingEmail no User
  → envia código de verificação para o NOVO email
  → email antigo continua funcionando

POST /auth/confirm-email-change  { code }
  → valida código
  → faz a troca efetiva (email = pendingEmail, pendingEmail = null)
  → envia email de notificação para o endereço ANTIGO ("seu email foi alterado")
```

**Mudança no modelo `User`:**
- Adicionar campo `pendingEmail` (nullable)

**Por que importa:** sem isso, um atacante que obteve acesso à conta pode trocar o email e "trancar" o dono para fora permanentemente.

**Complexidade:** ⭐⭐ Baixa

---

## Tier 2 — Médio prazo (adicionam valor significativo)

### 4. Alertas de segurança por email ✅

**Implementado em:** `SecurityAlertEventListener` (`infra/audit/`) + novos métodos em `EmailPort`.

**Eventos cobertos:** `USER_PASSWORD_CHANGED`, `ACCOUNT_LOCKED`, `TOTP_ENABLED`, `TOTP_DISABLED`, `TOKEN_THEFT_DETECTED`.

**Pendente (Tier 3):** alerta de login em novo dispositivo/IP (requer histórico de IP por usuário).

---

### 5. Histórico de acessos (login history)

**O que é:** o usuário pode ver os últimos N logins na conta dele, com IP, dispositivo e horário.

**Endpoints:**
```
GET /users/me/login-history?page=0&size=10
```

**Modelo novo:**
- `LoginEvent` em `core/domain/model/auth/`  
  — campos: `id`, `username`, `ipAddress`, `userAgent`, `loggedAt`, `status` (SUCCESS/FAILED)

**Valor:** transparência para o usuário + ele mesmo pode detectar acessos suspeitos.

**Complexidade:** ⭐⭐ Baixa

---

### 6. Limite de sessões simultâneas ✅

**Implementado em:** `RefreshTokenRepositoryImpl` — revoga automaticamente as sessões mais antigas ao emitir um novo token quando o limite é atingido.

**Configuração:** `auth.max-sessions-per-user` (padrão 5; `0` = sem limite).

---

### 7. Verificação de senha comprometida (HaveIBeenPwned)

**O que é:** ao criar ou trocar senha, consultar a API do HaveIBeenPwned para verificar se a senha já foi vazada em algum breach público.

**Como funciona (k-Anonymity — privacidade preservada):**
```
1. SHA1 da senha → "5BAA61E4C9B93F3F0682250B6CF8331B7EE68FD8"
2. Envia apenas os 5 primeiros chars: GET haveibeenpwned.com/range/5BAA6
3. API retorna sufixos de hashes comprometidos
4. Checa localmente se o sufixo da senha está na lista
5. Nunca envia a senha nem o hash completo para fora
```

**Onde integrar:** dentro de `PasswordPolicy.validate()` como uma validação opcional.

**Complexidade:** ⭐⭐ Baixa (uma chamada HTTP simples com lógica de k-Anonymity)

---

### 8. OAuth2 / Login social (Google) ✅ Implementado (via GIS ID Token)

**O que é:** permitir que o usuário se autentique com conta Google ou GitHub, sem criar senha no sistema.

**Fluxo:**
```
GET /oauth2/authorize/google  → redireciona para Google
← callback: /oauth2/callback/google?code=...
  → troca code por access token do Google
  → busca perfil (email, nome)
  → cria conta local (sem senha) ou vincula à conta existente
  → emite TokenPair do sistema
```

**Desafios de integração com a arquitetura atual:**
- Usuários OAuth não têm senha — o campo `password` pode ficar nulo (precisaria de nullable)
- Um usuário pode ter múltiplos providers (Google + GitHub + senha local) — nova entidade `OAuthConnection`
- O fluxo de verificação de email é diferente (email já é verificado pelo provider)

**Dependência:** Spring Security OAuth2 Client (já no ecossistema Spring Boot)

**Complexidade:** ⭐⭐⭐⭐ Alta (integração com providers externos + mudanças no modelo de User)

---

## Tier 3 — Longo prazo / casos específicos

### 9. API Keys (autenticação máquina-a-máquina)

**O que é:** tokens estáticos de longa duração para integrações B2B ou CLIs, sem o overhead de JWT + refresh.

**Modelo:** `ApiKey` com `keyPrefix` (exibível), `keyHash` (salvo), `name`, `scopes`, `expiresAt`, `lastUsedAt`

**Segurança:** a chave completa só é mostrada uma vez na criação (igual GitHub PAT). Salvar apenas o hash.

**Complexidade:** ⭐⭐⭐ Média

---

### 10. Sistema de convites

**O que é:** admin envia convite por email; o link permite o usuário criar sua conta sem passar pelo fluxo de registro público.

**Fluxo:**
```
POST /admin/users/invite  { email, roles }
  → gera token de convite (TTL configurável, ex: 72h)
  → envia email com link de cadastro pré-preenchido

POST /auth/register-invited  { inviteToken, username, password }
  → valida token
  → cria conta já com email verificado (não precisa de outro ciclo de verificação)
```

**Útil para:** apps corporativos, SaaS com onboarding manual, ambientes fechados.

**Complexidade:** ⭐⭐ Baixa (padrão similar ao password reset)

---

### 11. Impersonation (admin agir como usuário)

**O que é:** um administrador pode assumir temporariamente a identidade de um usuário para diagnosticar problemas.

**Implementação:**
```
POST /admin/users/{id}/impersonate
  → gera token JWT com claim extra: { "impersonatedBy": "admin@..." }
  → ação é registrada em auditoria

DELETE /auth/impersonate
  → retorna à sessão do admin
```

**Requer:** controle fino de permissões + audit trail obrigatório.

**Complexidade:** ⭐⭐⭐ Média

---

### 12. Suporte a i18n (internacionalização)

**O que é:** mensagens de erro da API e templates de email em múltiplos idiomas, definidos por `Accept-Language` ou preferência do usuário.

**Onde afeta:**
- `GlobalExceptionHandler` → mensagens de erro localizadas
- Templates de email → versões em `pt-BR`, `en-US`
- Validações do Bean Validation → mensagens customizadas

**Complexidade:** ⭐⭐⭐ Média (mudança transversal, afeta muitos arquivos)

---

### 13. Métricas com Micrometer + Prometheus ✅

**Implementado em:** `SecurityMetricsEventListener` (`infra/metrics/`) + counter `auth.rate_limit.blocked.total` no `LoginRateLimitingFilter`.

**Counters disponíveis em `/actuator/prometheus`:**
- `auth_login_total{result="success|failure"}`
- `auth_oauth_login_total{provider="google"}`
- `auth_rate_limit_blocked_total`
- `auth_account_locked_total`
- `auth_token_theft_total`
- `auth_sessions_cleared_total`
- `auth_password_reset_completed_total`
- `users_registered_total`
- `users_email_verified_total`

Container Prometheus incluído no `docker-compose.yml` (hml) — acesse `http://localhost:9090` após `docker compose up -d`.

---

### 14. Trusted Devices ("Lembrar este dispositivo" para 2FA)

**O que é:** ao completar 2FA num dispositivo, o usuário pode marcar como "confiável" por N dias. Nesse período, o 2FA não é exigido nesse dispositivo.

**Implementação:** cookie assinado com `deviceId` + tabela `trusted_device` com TTL.

**Só faz sentido após** o 2FA estar implementado.

**Complexidade:** ⭐⭐⭐ Média

---

### 15. Exclusão de conta + exportação de dados (GDPR)

**O que é:** o usuário pode solicitar a exclusão completa da própria conta ou exportar todos os dados pessoais armazenados.

**Fluxo de exclusão:**
```
DELETE /users/me  { password }
  → revoga todas as sessões
  → anonimiza ou remove dados pessoais (configurável: hard delete vs soft delete com anonimização)
  → envia email de confirmação de exclusão
```

**Exportação:**
```
GET /users/me/data-export
  → retorna JSON com todos os dados do usuário (perfil, sessões, login history, etc.)
```

**Complexidade:** ⭐⭐ Baixa (remoção) / ⭐⭐⭐ Média (exportação completa)

---

## Tier 2-B — DEV System (adicionados em 2026-06-02)

### 16. Controle de visibilidade de usuários DEV ✅

**O que é:** ADMIN não vê usuários com `ROLE_DEV` na listagem, não pode atribuir `ROLE_DEV`
e não visualiza eventos `DEV_ELEVATION_COMPLETED` nos audit logs.

**Implementado em:** `UserService` (filtro por role), `AuditLogsService` (filtro por action), V32–V37 migrations.

---

### 17. Elevação DEV com timer regressivo ✅

**O que é:** Duplo TOTP consecutivo (T e T+1) para emitir access token com authority `DEV_ELEVATED`
(TTL 1h, sem refresh). `DevAuthController` (`POST /auth/dev/first-code` + `POST /auth/dev/complete`).

**Implementado em:** `DevAuthController`, `TotpService`, `DevChallengeRepository`, V30 migration.

---

### 18. Grafana integrado ao painel DEV ✅

**O que é:** Prometheus scrapa `/actuator/prometheus` com autenticação por `ROLE_ADMIN`. Dashboard
com 22 painéis provisionado automaticamente em `grafana/provisioning/`.

**Implementado em:** `docker-compose.yml` (Grafana + Prometheus), `grafana/provisioning/dashboards/security-spring.json`, `grafana/provisioning/alerting/alerts.yml`.

---

### 19. Feature flags — configuração dinâmica do sistema ✅

**O que é:** Tabela `system_config` para ligar/desligar funcionalidades em runtime sem restart.
Flags: Google login, Google register, registro público, esqueci senha. Endpoint público
`GET /system/config/public`. Gerenciado via `PUT /system/config/{key}` (requer `DEV_ELEVATED`).

**Implementado em:** `SystemConfigService`, `SystemConfigController`, V34 migration.

---

## Resumo por prioridade

```
TIER 1 — Implementar antes de qualquer projeto real
┌──────────────────────────────────────────┬────────────┬──────────────┐
│ Funcionalidade                           │ Complexid. │ Status       │
├──────────────────────────────────────────┼────────────┼──────────────┤
│ 1. Password Reset                        │ ⭐⭐        │ ✅ Feito     │
│ 2. 2FA via TOTP                          │ ⭐⭐⭐       │ ✅ Feito     │
│ 3. Email change confirmation             │ ⭐⭐        │ ✅ Feito     │
└──────────────────────────────────────────┴────────────┴──────────────┘

TIER 2 — Adicionam valor significativo
┌──────────────────────────────────────────┬────────────┬──────────────┐
│ 4. Alertas de segurança por email        │ ⭐⭐        │ ✅ Feito     │
│ 5. Histórico de acessos                  │ ⭐⭐        │ Pendente     │
│ 6. Limite de sessões simultâneas         │ ⭐          │ ✅ Feito     │
│ 7. Verificação HaveIBeenPwned            │ ⭐⭐        │ Pendente     │
│ 8. OAuth2 / Login social (Google GIS)    │ ⭐⭐⭐⭐      │ ✅ Feito     │
│ 16. Visibilidade usuários DEV            │ ⭐          │ ✅ Feito     │
│ 17. Timer regressivo DEV (frontend)      │ ⭐⭐⭐       │ ✅ Feito     │
│ 18. Grafana integrado DEV System         │ ⭐⭐        │ ✅ Feito     │
│ 19. Feature flags sistema                │ ⭐⭐        │ ✅ Feito     │
└──────────────────────────────────────────┴────────────┴──────────────┘

TIER 3 — Casos específicos / projetos maduros
┌──────────────────────────────────────────┬────────────┬──────────────┐
│ 9.  API Keys (m2m)                       │ ⭐⭐⭐       │ Pendente     │
│ 10. Sistema de convites                  │ ⭐⭐        │ Pendente     │
│ 11. Impersonation (admin)                │ ⭐⭐⭐       │ Pendente     │
│ 12. i18n                                 │ ⭐⭐⭐       │ Pendente     │
│ 13. Métricas Micrometer/Prometheus       │ ⭐          │ ✅ Feito     │
│ 14. Trusted Devices (pós-2FA)            │ ⭐⭐⭐       │ Pendente     │
│ 15. GDPR (exclusão + exportação)         │ ⭐⭐/⭐⭐⭐   │ Pendente     │
└──────────────────────────────────────────┴────────────┴──────────────┘
```

---

*Última atualização: 2026-06-03*
