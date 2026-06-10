package com.targetmusic.adapter.in.converter;

import com.targetmusic.adapter.in.dtos.response.InstrumentoResponse;
import com.targetmusic.core.domain.model.instrumento.Instrumento;

public class InstrumentoDTOConverter {

    public InstrumentoResponse toResponse(Instrumento instrumento) {
        return new InstrumentoResponse(
                instrumento.getId(),
                instrumento.getTipo(),
                instrumento.getMarca(),
                instrumento.getModelo(),
                instrumento.getNumeroDeSerie(),
                instrumento.getCor(),
                instrumento.getDescricao(),
                instrumento.getClienteId(),
                instrumento.getCreatedAt()
        );
    }
}
