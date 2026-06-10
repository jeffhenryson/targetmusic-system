package com.targetmusic.core.domain.exception.instrumento;

public class InstrumentoTemOSEmAbertoException extends RuntimeException {

    public InstrumentoTemOSEmAbertoException(Long instrumentoId) {
        super("Instrumento possui ordens de serviço em aberto: instrumentoId=" + instrumentoId);
    }
}
