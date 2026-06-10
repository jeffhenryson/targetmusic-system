package com.targetmusic.adapter.in.dtos.request;

import java.time.LocalDate;

public record OSUpdateRequest(
        String laudoTecnico,
        LocalDate prazoEstimado,
        String observacoes
) {}
