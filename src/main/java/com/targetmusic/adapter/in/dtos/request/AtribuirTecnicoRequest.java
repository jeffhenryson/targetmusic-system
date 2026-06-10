package com.targetmusic.adapter.in.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtribuirTecnicoRequest(
        @NotBlank String tecnicoUsername,
        @NotNull AcaoTecnico acao
) {
    public enum AcaoTecnico { ADICIONAR, REMOVER }
}
