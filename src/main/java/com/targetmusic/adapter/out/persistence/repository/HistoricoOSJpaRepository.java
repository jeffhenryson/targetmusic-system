package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.entity.HistoricoOSEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoOSJpaRepository extends JpaRepository<HistoricoOSEntity, Long> {

    List<HistoricoOSEntity> findByOsIdOrderByTimestampAsc(Long osId);
}
