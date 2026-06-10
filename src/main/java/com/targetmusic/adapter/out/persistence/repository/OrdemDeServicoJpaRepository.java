package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.entity.OrdemDeServicoEntity;
import com.targetmusic.core.domain.model.os.StatusOS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdemDeServicoJpaRepository extends JpaRepository<OrdemDeServicoEntity, Long> {

    Optional<OrdemDeServicoEntity> findByNumero(String numero);

    List<OrdemDeServicoEntity> findByClienteId(Long clienteId);

    @Query(value = "SELECT DISTINCT o FROM OrdemDeServicoEntity o LEFT JOIN o.tecnicosUsernames t " +
                   "WHERE (:status IS NULL OR o.status = :status) " +
                   "AND (:clienteId IS NULL OR o.clienteId = :clienteId) " +
                   "AND (:tecnicoUsername IS NULL OR t = :tecnicoUsername)",
           countQuery = "SELECT COUNT(DISTINCT o) FROM OrdemDeServicoEntity o LEFT JOIN o.tecnicosUsernames t " +
                        "WHERE (:status IS NULL OR o.status = :status) " +
                        "AND (:clienteId IS NULL OR o.clienteId = :clienteId) " +
                        "AND (:tecnicoUsername IS NULL OR t = :tecnicoUsername)")
    Page<OrdemDeServicoEntity> findFiltered(@Param("status") StatusOS status,
                                             @Param("clienteId") Long clienteId,
                                             @Param("tecnicoUsername") String tecnicoUsername,
                                             Pageable pageable);
}
