package com.targetmusic.adapter.out.persistence.converter;

import com.targetmusic.adapter.out.persistence.entity.InstrumentoEntity;
import com.targetmusic.core.domain.model.instrumento.Instrumento;

public class InstrumentoEntityConverter {

    public Instrumento toDomain(InstrumentoEntity entity) {
        return Instrumento.fromPersisted(
                entity.getId(),
                entity.getTipo(),
                entity.getMarca(),
                entity.getModelo(),
                entity.getNumeroDeSerie(),
                entity.getCor(),
                entity.getDescricao(),
                entity.getClienteId(),
                entity.getCreatedAt()
        );
    }

    public InstrumentoEntity toEntity(Instrumento domain) {
        InstrumentoEntity entity = new InstrumentoEntity();
        entity.setId(domain.getId());
        entity.setTipo(domain.getTipo());
        entity.setMarca(domain.getMarca());
        entity.setModelo(domain.getModelo());
        entity.setNumeroDeSerie(domain.getNumeroDeSerie());
        entity.setCor(domain.getCor());
        entity.setDescricao(domain.getDescricao());
        entity.setClienteId(domain.getClienteId());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
