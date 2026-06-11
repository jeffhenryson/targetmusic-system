package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByUserId(Long userId);

    @Query(value = "SELECT c.* FROM clientes c JOIN users u ON c.user_id = u.id WHERE u.username = :username",
           nativeQuery = true)
    Optional<ClienteEntity> findByUserUsername(@Param("username") String username);

    @Query("SELECT c FROM ClienteEntity c WHERE :search IS NULL OR " +
           "LOWER(c.nome) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "c.telefone LIKE CONCAT('%', :search, '%')")
    Page<ClienteEntity> findFiltered(@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM ordens_de_servico " +
                   "WHERE cliente_id = :clienteId AND status NOT IN ('ENTREGUE', 'CANCELADO'))",
           nativeQuery = true)
    boolean hasOpenOS(@Param("clienteId") Long clienteId);
}
