package com.targetmusic.adapter.out.persistence.entity;

import com.targetmusic.core.domain.model.os.StatusOS;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "historico_os")
public class HistoricoOSEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "os_id", nullable = false)
    private Long osId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 30)
    private StatusOS statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 30)
    private StatusOS statusNovo;

    @Column(name = "usuario_username", nullable = false, length = 80)
    private String usuarioUsername;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(nullable = false)
    private Instant timestamp;
}
