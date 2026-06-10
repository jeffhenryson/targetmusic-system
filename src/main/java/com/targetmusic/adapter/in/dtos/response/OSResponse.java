package com.targetmusic.adapter.in.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public record OSResponse(
        Long id,
        String numero,
        String status,
        InstrumentoInfo instrumento,
        ClienteInfo cliente,
        String atendenteUsername,
        Set<String> tecnicosUsernames,
        String descricaoProblema,
        String laudoTecnico,
        BigDecimal valorOrcamento,
        BigDecimal valorFinal,
        LocalDate prazoEstimado,
        Instant dataRecebimento,
        Instant dataEntrega,
        String observacoes,
        Instant createdAt,
        Instant updatedAt
) {
    public record InstrumentoInfo(Long id, String tipo, String marca, String modelo) {}
    public record ClienteInfo(Long id, String nome, String telefone) {}
}
