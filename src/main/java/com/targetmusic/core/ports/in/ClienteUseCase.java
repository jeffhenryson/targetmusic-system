package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ClienteUseCase {
    Cliente criar(String nome, String telefone, String email, String cpf, String endereco, String observacoes);
    Cliente buscarPorId(Long id);
    Optional<Cliente> buscarPorUserId(Long userId);
    Optional<Cliente> buscarClienteDoUsuario(String username);
    Map<Long, Cliente> buscarPorIds(Collection<Long> ids);
    PageResult<Cliente> listar(String search, int page, int size);
    Cliente atualizar(Long id, String nome, String telefone, String email, String cpf, String endereco, String observacoes);
    void remover(Long id);
    void vincularUsuario(Long clienteId, Long userId);
}
