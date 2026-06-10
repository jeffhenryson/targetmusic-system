package com.targetmusic.core.domain.exception.estoque;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String nomePeca, int solicitado, int disponivel) {
        super("Estoque insuficiente para '%s': solicitado=%d, disponível=%d"
                .formatted(nomePeca, solicitado, disponivel));
    }
}
