package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.converter.ClienteEntityConverter;
import com.targetmusic.adapter.out.persistence.entity.ClienteEntity;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.ports.out.cliente.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class ClienteRepositoryImpl implements ClienteRepository {

    private final ClienteJpaRepository jpaRepo;
    private final ClienteEntityConverter converter;

    public ClienteRepositoryImpl(ClienteJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
        this.converter = new ClienteEntityConverter();
    }

    @Override
    @Transactional
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = converter.toEntity(cliente);
        return converter.toDomain(jpaRepo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> findById(Long id) {
        return jpaRepo.findById(id).map(converter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> findByUserId(Long userId) {
        return jpaRepo.findByUserId(userId).map(converter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Cliente> findAll(String search, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("nome").ascending());
        Page<ClienteEntity> result = jpaRepo.findFiltered(search, pageable);
        return new PageResult<>(
                result.getContent().stream().map(converter::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOpenOS(Long clienteId) {
        return jpaRepo.hasOpenOS(clienteId);
    }
}
