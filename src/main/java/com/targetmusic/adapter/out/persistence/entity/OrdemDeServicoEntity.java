package com.targetmusic.adapter.out.persistence.entity;

import com.targetmusic.core.domain.model.os.StatusOS;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ordens_de_servico")
public class OrdemDeServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOS status;

    @Column(name = "instrumento_id", nullable = false)
    private Long instrumentoId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "atendente_username", nullable = false, length = 80)
    private String atendenteUsername;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "os_tecnicos", joinColumns = @JoinColumn(name = "os_id"))
    @Column(name = "tecnico_username", length = 80)
    private Set<String> tecnicosUsernames = new HashSet<>();

    @Column(name = "descricao_problema", nullable = false, columnDefinition = "TEXT")
    private String descricaoProblema;

    @Column(name = "laudo_tecnico", columnDefinition = "TEXT")
    private String laudoTecnico;

    @Column(name = "valor_orcamento", precision = 10, scale = 2)
    private BigDecimal valorOrcamento;

    @Column(name = "valor_final", precision = 10, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "prazo_estimado")
    private LocalDate prazoEstimado;

    @Column(name = "data_recebimento", nullable = false)
    private Instant dataRecebimento;

    @Column(name = "data_entrega")
    private Instant dataEntrega;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
