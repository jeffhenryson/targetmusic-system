package com.targetmusic.adapter.in.dtos.response;

import java.time.Instant;

public record HistoricoOSResponse(
        Long id,
        Long osId,
        String statusAnterior,
        String statusNovo,
        String usuarioUsername,
        String observacao,
        Instant timestamp
) {}
