package com.targetmusic.core.domain.model.os;

import java.math.BigDecimal;
import java.time.Instant;

public record OSPeca(
        Long id,
        Long osId,
        Long pecaId,
        String pecaNome,           // snapshot — preserva histórico se peça for renomeada
        int quantidade,
        BigDecimal precoUnitario,  // snapshot do preço no momento do uso
        String tecnicoUsername,
        Instant addedAt
) {
    public BigDecimal subtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
