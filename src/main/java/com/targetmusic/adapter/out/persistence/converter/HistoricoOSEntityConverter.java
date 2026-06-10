package com.targetmusic.adapter.out.persistence.converter;

import com.targetmusic.adapter.out.persistence.entity.HistoricoOSEntity;
import com.targetmusic.core.domain.model.os.HistoricoOS;

public class HistoricoOSEntityConverter {

    public HistoricoOS toDomain(HistoricoOSEntity entity) {
        return new HistoricoOS(
                entity.getId(),
                entity.getOsId(),
                entity.getStatusAnterior(),
                entity.getStatusNovo(),
                entity.getUsuarioUsername(),
                entity.getObservacao(),
                entity.getTimestamp()
        );
    }

    public HistoricoOSEntity toEntity(HistoricoOS historico) {
        HistoricoOSEntity entity = new HistoricoOSEntity();
        entity.setId(historico.id());
        entity.setOsId(historico.osId());
        entity.setStatusAnterior(historico.statusAnterior());
        entity.setStatusNovo(historico.statusNovo());
        entity.setUsuarioUsername(historico.usuarioUsername());
        entity.setObservacao(historico.observacao());
        entity.setTimestamp(historico.timestamp());
        return entity;
    }
}
