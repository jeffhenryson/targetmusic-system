package com.targetmusic.core.domain.exception.instrumento;

public class InstrumentoNotFoundException extends RuntimeException {

    public InstrumentoNotFoundException(Long id) {
        super("Instrumento não encontrado: id=" + id);
    }
}
