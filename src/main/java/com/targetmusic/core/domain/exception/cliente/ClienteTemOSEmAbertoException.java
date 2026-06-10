package com.targetmusic.core.domain.exception.cliente;

public class ClienteTemOSEmAbertoException extends RuntimeException {

    public ClienteTemOSEmAbertoException(Long clienteId) {
        super("Cliente possui ordens de serviço em aberto: clienteId=" + clienteId);
    }
}
