package com.targetmusic.core.domain.model.os;

import java.time.Instant;

public record HistoricoOS(
        Long id,
        Long osId,
        StatusOS statusAnterior,  // null = criação da OS
        StatusOS statusNovo,
        String usuarioUsername,
        String observacao,        // nullable
        Instant timestamp
) {
}
