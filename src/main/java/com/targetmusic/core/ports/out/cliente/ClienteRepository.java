package com.targetmusic.core.ports.out.cliente;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByUserId(Long userId);
    Optional<Cliente> findByUserUsername(String username);
    List<Cliente> findAllByIdIn(Collection<Long> ids);
    PageResult<Cliente> findAll(String search, int page, int size);
    void deleteById(Long id);
    boolean hasOpenOS(Long clienteId);
}
