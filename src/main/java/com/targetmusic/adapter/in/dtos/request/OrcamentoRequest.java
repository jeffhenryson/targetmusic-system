package com.targetmusic.adapter.in.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrcamentoRequest(
        @NotNull @DecimalMin("0.01") BigDecimal valor,
        LocalDate prazoEstimado
) {}
