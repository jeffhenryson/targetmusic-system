package com.targetmusic.adapter.in.dtos.response;

import java.time.Instant;

public record ClienteResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        String cpf,
        String endereco,
        String observacoes,
        Long userId,
        Instant createdAt
) {}
