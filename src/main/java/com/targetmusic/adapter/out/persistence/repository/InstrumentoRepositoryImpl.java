package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.converter.InstrumentoEntityConverter;
import com.targetmusic.adapter.out.persistence.entity.InstrumentoEntity;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.ports.out.instrumento.InstrumentoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Instrumento> findByClienteId(Long clienteId) {
        return jpaRepo.findByClienteId(clienteId).stream()
                .map(converter::toDomain)
                .toList();
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
