package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.entity.InstrumentoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstrumentoJpaRepository extends JpaRepository<InstrumentoEntity, Long> {

    Page<InstrumentoEntity> findByClienteId(Long clienteId, Pageable pageable);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM ordens_de_servico " +
                   "WHERE instrumento_id = :instrumentoId AND status NOT IN ('ENTREGUE', 'CANCELADO'))",
           nativeQuery = true)
    boolean hasOpenOS(@Param("instrumentoId") Long instrumentoId);
}
