package com.targetmusic.core.domain.exception.estoque;

public class PecaNotFoundException extends RuntimeException {

    public PecaNotFoundException(Long id) {
        super("Peça não encontrada: id=" + id);
    }
}
