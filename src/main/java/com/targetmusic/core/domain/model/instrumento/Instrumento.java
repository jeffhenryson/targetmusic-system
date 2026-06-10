package com.targetmusic.core.domain.model.instrumento;

import java.time.Instant;
import java.util.Objects;

public class Instrumento {

    private Long id;
    private TipoInstrumento tipo;
    private String marca;
    private String modelo;
    private String numeroDeSerie;
    private String cor;
    private String descricao;
    private Long clienteId;
    private Instant createdAt;

    Instrumento() {
    }

    public static Instrumento of(TipoInstrumento tipo, String marca, String modelo, Long clienteId) {
        Objects.requireNonNull(tipo, "tipo is required");
        Objects.requireNonNull(marca, "marca is required");
        Objects.requireNonNull(modelo, "modelo is required");
        Objects.requireNonNull(clienteId, "clienteId is required");
        Instrumento i = new Instrumento();
        i.tipo = tipo;
        i.marca = marca;
        i.modelo = modelo;
        i.clienteId = clienteId;
        return i;
    }

    public static Instrumento fromPersisted(Long id, TipoInstrumento tipo, String marca, String modelo,
                                             String numeroDeSerie, String cor, String descricao,
                                             Long clienteId, Instant createdAt) {
        Instrumento i = new Instrumento();
        i.id = id;
        i.tipo = tipo;
        i.marca = marca;
        i.modelo = modelo;
        i.numeroDeSerie = numeroDeSerie;
        i.cor = cor;
        i.descricao = descricao;
        i.clienteId = clienteId;
        i.createdAt = createdAt;
        return i;
    }

    public void atualizar(TipoInstrumento tipo, String marca, String modelo,
                          String numeroDeSerie, String cor, String descricao) {
        Objects.requireNonNull(tipo, "tipo is required");
        Objects.requireNonNull(marca, "marca is required");
        Objects.requireNonNull(modelo, "modelo is required");
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.numeroDeSerie = numeroDeSerie;
        this.cor = cor;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public TipoInstrumento getTipo() { return tipo; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getNumeroDeSerie() { return numeroDeSerie; }
    public String getCor() { return cor; }
    public String getDescricao() { return descricao; }
    public Long getClienteId() { return clienteId; }
    public Instant getCreatedAt() { return createdAt; }
}
