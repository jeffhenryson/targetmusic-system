package com.targetmusic.adapter.in.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OSRequest(
        @NotNull Long instrumentoId,
        @NotNull Long clienteId,
        @NotBlank String descricaoProblema,
        String observacoes
) {}
