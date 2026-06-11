package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.converter.InstrumentoEntityConverter;
import com.targetmusic.adapter.out.persistence.entity.InstrumentoEntity;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.ports.out.instrumento.InstrumentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class InstrumentoRepositoryImpl implements InstrumentoRepository {

    private final InstrumentoJpaRepository jpaRepo;
    private final InstrumentoEntityConverter converter;

    public InstrumentoRepositoryImpl(InstrumentoJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
        this.converter = new InstrumentoEntityConverter();
    }

    @Override
    @Transactional
    public Instrumento save(Instrumento instrumento) {
        InstrumentoEntity entity = converter.toEntity(instrumento);
        return converter.toDomain(jpaRepo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instrumento> findById(Long id) {
        return jpaRepo.findById(id).map(converter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Instrumento> findAllByIdIn(Collection<Long> ids) {
        return jpaRepo.findAllById(ids).stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Instrumento> findByClienteId(Long clienteId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<InstrumentoEntity> result = jpaRepo.findByClienteId(clienteId, pageable);
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
    public boolean hasOpenOS(Long instrumentoId) {
        return jpaRepo.hasOpenOS(instrumentoId);
    }
}
