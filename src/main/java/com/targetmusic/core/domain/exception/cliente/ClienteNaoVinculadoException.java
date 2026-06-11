package com.targetmusic.core.domain.exception.cliente;

public class ClienteNaoVinculadoException extends RuntimeException {

    public ClienteNaoVinculadoException(String username) {
        super("Usuário não está vinculado a nenhum cliente: username=" + username);
    }
}
