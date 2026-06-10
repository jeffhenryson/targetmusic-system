package com.targetmusic.core.domain.exception.cliente;

public class ClienteNotFoundException extends RuntimeException {

    public ClienteNotFoundException(Long id) {
        super("Cliente não encontrado: id=" + id);
    }
}
