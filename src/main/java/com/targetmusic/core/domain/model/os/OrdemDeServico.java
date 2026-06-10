package com.targetmusic.core.domain.model.os;

import com.targetmusic.core.domain.exception.os.TransicaoStatusInvalidaException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OrdemDeServico {

    private Long id;
    private String numero;
    private StatusOS status;
    private Long instrumentoId;
    private Long clienteId;
    private String atendenteUsername;
    private Set<String> tecnicosUsernames = new HashSet<>();
    private String descricaoProblema;
    private String laudoTecnico;
    private BigDecimal valorOrcamento;
    private BigDecimal valorFinal;
    private LocalDate prazoEstimado;
    private Instant dataRecebimento;
    private Instant dataEntrega;
    private String observacoes;
    private Instant createdAt;
    private Instant updatedAt;

    OrdemDeServico() {
    }

    public static OrdemDeServico abrir(String numero, Long instrumentoId, Long clienteId,
                                        String atendenteUsername, String descricaoProblema) {
        Objects.requireNonNull(numero, "numero is required");
        Objects.requireNonNull(instrumentoId, "instrumentoId is required");
        Objects.requireNonNull(clienteId, "clienteId is required");
        Objects.requireNonNull(atendenteUsername, "atendenteUsername is required");
        Objects.requireNonNull(descricaoProblema, "descricaoProblema is required");
        OrdemDeServico os = new OrdemDeServico();
        os.numero = numero;
        os.instrumentoId = instrumentoId;
        os.clienteId = clienteId;
        os.atendenteUsername = atendenteUsername;
        os.descricaoProblema = descricaoProblema;
        os.status = StatusOS.RECEBIDO;
        os.dataRecebimento = Instant.now();
        return os;
    }

    public static OrdemDeServico fromPersisted(Long id, String numero, StatusOS status,
                                                Long instrumentoId, Long clienteId,
                                                String atendenteUsername, Set<String> tecnicosUsernames,
                                                String descricaoProblema, String laudoTecnico,
                                                BigDecimal valorOrcamento, BigDecimal valorFinal,
                                                LocalDate prazoEstimado, Instant dataRecebimento,
                                                Instant dataEntrega, String observacoes,
                                                Instant createdAt, Instant updatedAt) {
        OrdemDeServico os = new OrdemDeServico();
        os.id = id;
        os.numero = numero;
        os.status = status;
        os.instrumentoId = instrumentoId;
        os.clienteId = clienteId;
        os.atendenteUsername = atendenteUsername;
        os.tecnicosUsernames = tecnicosUsernames != null ? new HashSet<>(tecnicosUsernames) : new HashSet<>();
        os.descricaoProblema = descricaoProblema;
        os.laudoTecnico = laudoTecnico;
        os.valorOrcamento = valorOrcamento;
        os.valorFinal = valorFinal;
        os.prazoEstimado = prazoEstimado;
        os.dataRecebimento = dataRecebimento;
        os.dataEntrega = dataEntrega;
        os.observacoes = observacoes;
        os.createdAt = createdAt;
        os.updatedAt = updatedAt;
        return os;
    }

    public void mudarStatus(StatusOS novoStatus) {
        Objects.requireNonNull(novoStatus, "novoStatus is required");
        if (!StatusOS.transicaoValida(this.status, novoStatus)) {
            throw new TransicaoStatusInvalidaException(this.status, novoStatus);
        }
        this.status = novoStatus;
    }

    public void adicionarTecnico(String tecnicoUsername) {
        Objects.requireNonNull(tecnicoUsername, "tecnicoUsername is required");
        this.tecnicosUsernames.add(tecnicoUsername);
    }

    public void removerTecnico(String tecnicoUsername) {
        this.tecnicosUsernames.remove(tecnicoUsername);
    }

    public void atualizarLaudo(String laudoTecnico) {
        this.laudoTecnico = laudoTecnico;
    }

    public void definirOrcamento(BigDecimal valor) {
        Objects.requireNonNull(valor, "valor is required");
        this.valorOrcamento = valor;
    }

    public void definirPrazo(LocalDate prazoEstimado) {
        this.prazoEstimado = prazoEstimado;
    }

    public void registrarEntrega() {
        mudarStatus(StatusOS.ENTREGUE);
        this.dataEntrega = Instant.now();
    }

    public void definirValorFinal(BigDecimal valor) {
        this.valorFinal = valor;
    }

    public void definirObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public StatusOS getStatus() { return status; }
    public Long getInstrumentoId() { return instrumentoId; }
    public Long getClienteId() { return clienteId; }
    public String getAtendenteUsername() { return atendenteUsername; }
    public Set<String> getTecnicosUsernames() { return new HashSet<>(tecnicosUsernames); }
    public String getDescricaoProblema() { return descricaoProblema; }
    public String getLaudoTecnico() { return laudoTecnico; }
    public BigDecimal getValorOrcamento() { return valorOrcamento; }
    public BigDecimal getValorFinal() { return valorFinal; }
    public LocalDate getPrazoEstimado() { return prazoEstimado; }
    public Instant getDataRecebimento() { return dataRecebimento; }
    public Instant getDataEntrega() { return dataEntrega; }
    public String getObservacoes() { return observacoes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
