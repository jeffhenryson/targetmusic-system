package com.targetmusic.core.domain.exception.os;

public class OrdemDeServicoNotFoundException extends RuntimeException {

    public OrdemDeServicoNotFoundException(Long id) {
        super("Ordem de serviço não encontrada: id=" + id);
    }

    public OrdemDeServicoNotFoundException(String numero) {
        super("Ordem de serviço não encontrada: numero=" + numero);
    }
}
