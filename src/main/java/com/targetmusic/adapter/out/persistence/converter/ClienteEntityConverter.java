package com.targetmusic.adapter.out.persistence.converter;

import com.targetmusic.adapter.out.persistence.entity.ClienteEntity;
import com.targetmusic.core.domain.model.cliente.Cliente;

public class ClienteEntityConverter {

    public Cliente toDomain(ClienteEntity entity) {
        return Cliente.fromPersisted(
                entity.getId(),
                entity.getNome(),
                entity.getTelefone(),
                entity.getEmail(),
                entity.getCpf(),
                entity.getEndereco(),
                entity.getObservacoes(),
                entity.getUserId(),
                entity.getCreatedAt()
        );
    }

    public ClienteEntity toEntity(Cliente domain) {
        ClienteEntity entity = new ClienteEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setTelefone(domain.getTelefone());
        entity.setEmail(domain.getEmail());
        entity.setCpf(domain.getCpf());
        entity.setEndereco(domain.getEndereco());
        entity.setObservacoes(domain.getObservacoes());
        entity.setUserId(domain.getUserId());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
