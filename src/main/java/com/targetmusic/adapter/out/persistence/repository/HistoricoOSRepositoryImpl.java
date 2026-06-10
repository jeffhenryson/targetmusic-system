package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.converter.HistoricoOSEntityConverter;
import com.targetmusic.core.domain.model.os.HistoricoOS;
import com.targetmusic.core.ports.out.os.HistoricoOSRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class HistoricoOSRepositoryImpl implements HistoricoOSRepository {

    private final HistoricoOSJpaRepository jpaRepo;
    private final HistoricoOSEntityConverter converter;

    public HistoricoOSRepositoryImpl(HistoricoOSJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
        this.converter = new HistoricoOSEntityConverter();
    }

    @Override
    @Transactional
    public HistoricoOS save(HistoricoOS historico) {
        return converter.toDomain(jpaRepo.save(converter.toEntity(historico)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricoOS> findByOsId(Long osId) {
        return jpaRepo.findByOsIdOrderByTimestampAsc(osId).stream()
                .map(converter::toDomain)
                .toList();
    }
}
