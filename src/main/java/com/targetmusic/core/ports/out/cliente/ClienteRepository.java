package com.targetmusic.core.ports.out.cliente;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;

import java.util.Optional;

public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByUserId(Long userId);
    PageResult<Cliente> findAll(String search, int page, int size);
    void deleteById(Long id);
    boolean hasOpenOS(Long clienteId);
}
