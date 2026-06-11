package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.converter.OrdemDeServicoEntityConverter;
import com.targetmusic.adapter.out.persistence.entity.OrdemDeServicoEntity;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.domain.model.os.StatusOS;
import com.targetmusic.core.ports.out.os.OrdemDeServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class OrdemDeServicoRepositoryImpl implements OrdemDeServicoRepository {

    private final OrdemDeServicoJpaRepository jpaRepo;
    private final OrdemDeServicoEntityConverter converter;

    public OrdemDeServicoRepositoryImpl(OrdemDeServicoJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
        this.converter = new OrdemDeServicoEntityConverter();
    }

    @Override
    @Transactional
    public OrdemDeServico save(OrdemDeServico os) {
        OrdemDeServicoEntity entity = converter.toEntity(os);
        return converter.toDomain(jpaRepo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdemDeServico> findById(Long id) {
        return jpaRepo.findById(id).map(converter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdemDeServico> findByNumero(String numero) {
        return jpaRepo.findByNumero(numero).map(converter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<OrdemDeServico> findAll(StatusOS status, Long clienteId,
                                               String tecnicoUsername, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrdemDeServicoEntity> result = jpaRepo.findFiltered(status, clienteId, tecnicoUsername, pageable);
        return new PageResult<>(
                result.getContent().stream().map(converter::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<OrdemDeServico> findByClienteId(Long clienteId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrdemDeServicoEntity> result = jpaRepo.findByClienteId(clienteId, pageable);
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
}
