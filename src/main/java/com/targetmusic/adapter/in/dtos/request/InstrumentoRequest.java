package com.targetmusic.adapter.in.dtos.request;

import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InstrumentoRequest(
        @NotNull Long clienteId,
        @NotNull TipoInstrumento tipo,
        @NotBlank @Size(max = 100) String marca,
        @NotBlank @Size(max = 100) String modelo,
        @Size(max = 100) String numeroDeSerie,
        @Size(max = 50) String cor,
        String descricao
) {}
