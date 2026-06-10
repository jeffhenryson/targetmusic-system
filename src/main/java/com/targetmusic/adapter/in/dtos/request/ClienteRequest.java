package com.targetmusic.adapter.in.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Size(max = 20) String telefone,
        @Email @Size(max = 150) String email,
        @Size(max = 14) String cpf,
        @Size(max = 255) String endereco,
        String observacoes
) {}
