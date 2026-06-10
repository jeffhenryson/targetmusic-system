package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;

public interface ClienteUseCase {
    Cliente criar(String nome, String telefone, String email, String cpf, String endereco, String observacoes);
    Cliente buscarPorId(Long id);
    PageResult<Cliente> listar(String search, int page, int size);
    Cliente atualizar(Long id, String nome, String telefone, String email, String cpf, String endereco, String observacoes);
    void remover(Long id);
    void vincularUsuario(Long clienteId, Long userId);
}
