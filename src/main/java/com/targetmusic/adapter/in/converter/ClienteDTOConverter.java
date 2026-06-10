package com.targetmusic.adapter.in.converter;

import com.targetmusic.adapter.in.dtos.response.ClienteResponse;
import com.targetmusic.core.domain.model.cliente.Cliente;

public class ClienteDTOConverter {

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getCpf(),
                cliente.getEndereco(),
                cliente.getObservacoes(),
                cliente.getUserId(),
                cliente.getCreatedAt()
        );
    }
}
