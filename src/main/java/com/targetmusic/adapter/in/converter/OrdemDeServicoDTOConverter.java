package com.targetmusic.adapter.in.converter;

import com.targetmusic.adapter.in.dtos.response.HistoricoOSResponse;
import com.targetmusic.adapter.in.dtos.response.OSResponse;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.os.HistoricoOS;
import com.targetmusic.core.domain.model.os.OrdemDeServico;

public class OrdemDeServicoDTOConverter {

    public OSResponse toResponse(OrdemDeServico os, Instrumento instrumento, Cliente cliente) {
        return new OSResponse(
                os.getId(),
                os.getNumero(),
                os.getStatus().name(),
                new OSResponse.InstrumentoInfo(instrumento.getId(), instrumento.getTipo().name(),
                        instrumento.getMarca(), instrumento.getModelo()),
                new OSResponse.ClienteInfo(cliente.getId(), cliente.getNome(), cliente.getTelefone()),
                os.getAtendenteUsername(),
                os.getTecnicosUsernames(),
                os.getDescricaoProblema(),
                os.getLaudoTecnico(),
                os.getValorOrcamento(),
                os.getValorFinal(),
                os.getPrazoEstimado(),
                os.getDataRecebimento(),
                os.getDataEntrega(),
                os.getObservacoes(),
                os.getCreatedAt(),
                os.getUpdatedAt()
        );
    }

    public HistoricoOSResponse toHistoricoResponse(HistoricoOS h) {
        return new HistoricoOSResponse(
                h.id(),
                h.osId(),
                h.statusAnterior() != null ? h.statusAnterior().name() : null,
                h.statusNovo().name(),
                h.usuarioUsername(),
                h.observacao(),
                h.timestamp()
        );
    }
}
