package com.targetmusic.adapter.in.dtos.request;

import com.targetmusic.core.domain.model.os.StatusOS;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(
        @NotNull StatusOS novoStatus,
        String observacao
) {}
