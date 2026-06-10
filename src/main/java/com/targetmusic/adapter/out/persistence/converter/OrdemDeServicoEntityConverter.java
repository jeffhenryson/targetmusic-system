package com.targetmusic.adapter.out.persistence.converter;

import com.targetmusic.adapter.out.persistence.entity.OrdemDeServicoEntity;
import com.targetmusic.core.domain.model.os.OrdemDeServico;

import java.util.HashSet;

public class OrdemDeServicoEntityConverter {

    public OrdemDeServico toDomain(OrdemDeServicoEntity entity) {
        return OrdemDeServico.fromPersisted(
                entity.getId(),
                entity.getNumero(),
                entity.getStatus(),
                entity.getInstrumentoId(),
                entity.getClienteId(),
                entity.getAtendenteUsername(),
                new HashSet<>(entity.getTecnicosUsernames()),
                entity.getDescricaoProblema(),
                entity.getLaudoTecnico(),
                entity.getValorOrcamento(),
                entity.getValorFinal(),
                entity.getPrazoEstimado(),
                entity.getDataRecebimento(),
                entity.getDataEntrega(),
                entity.getObservacoes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public OrdemDeServicoEntity toEntity(OrdemDeServico os) {
        OrdemDeServicoEntity entity = new OrdemDeServicoEntity();
        entity.setId(os.getId());
        entity.setNumero(os.getNumero());
        entity.setStatus(os.getStatus());
        entity.setInstrumentoId(os.getInstrumentoId());
        entity.setClienteId(os.getClienteId());
        entity.setAtendenteUsername(os.getAtendenteUsername());
        entity.setTecnicosUsernames(new HashSet<>(os.getTecnicosUsernames()));
        entity.setDescricaoProblema(os.getDescricaoProblema());
        entity.setLaudoTecnico(os.getLaudoTecnico());
        entity.setValorOrcamento(os.getValorOrcamento());
        entity.setValorFinal(os.getValorFinal());
        entity.setPrazoEstimado(os.getPrazoEstimado());
        entity.setDataRecebimento(os.getDataRecebimento());
        entity.setDataEntrega(os.getDataEntrega());
        entity.setObservacoes(os.getObservacoes());
        return entity;
    }
}
