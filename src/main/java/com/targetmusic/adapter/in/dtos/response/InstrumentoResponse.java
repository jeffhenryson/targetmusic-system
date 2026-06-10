package com.targetmusic.adapter.in.dtos.response;

import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;

import java.time.Instant;

public record InstrumentoResponse(
        Long id,
        TipoInstrumento tipo,
        String marca,
        String modelo,
        String numeroDeSerie,
        String cor,
        String descricao,
        Long clienteId,
        Instant createdAt
) {}
