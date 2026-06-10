# 05 — Integrações Externas

## Email (herdado do template)

Notificações de segurança (login, senha alterada, etc.) usam a infraestrutura existente:
- Dev: `LoggingEmailAdapter` (imprime no console)
- Hml/Prod: Resend.com via `ResendEmailAdapter`

### Notificações de OS por email (planejado)

Futuros eventos que podem acionar emails ao cliente:
- OS aberta (confirmação de recebimento)
- Orçamento enviado (link para aprovação online — planejado)
- OS pronta (instrumento pode ser retirado)
- OS cancelada

Implementação: expandir `EmailPort` com novos métodos e criar templates Thymeleaf correspondentes.

---

## Notificações SSE (herdado do template)

Sistema de notificações in-app via Server-Sent Events já está implementado.
Pode ser usado para notificar técnicos/atendentes em tempo real sobre mudanças de status.

---

## Integrações futuras (não planejadas)

| Integração | Utilidade | Complexidade |
|------------|-----------|--------------|
| WhatsApp (Twilio/Z-API) | Notificar cliente via WhatsApp | ⭐⭐⭐ |
| Mercado Pago / PagSeguro | Pagamento online do orçamento | ⭐⭐⭐⭐ |
| NF-e | Emissão de nota fiscal eletrônica | ⭐⭐⭐⭐ |
