package com.targetmusic.adapter.in.dtos.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record EntregaRequest(
        @DecimalMin("0.00") BigDecimal valorFinal
) {}
