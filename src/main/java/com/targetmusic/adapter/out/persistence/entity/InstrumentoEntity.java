package com.targetmusic.adapter.out.persistence.entity;

import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "instrumentos")
public class InstrumentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoInstrumento tipo;

    @Column(nullable = false, length = 100)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(name = "numero_de_serie", length = 100)
    private String numeroDeSerie;

    @Column(length = 50)
    private String cor;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
