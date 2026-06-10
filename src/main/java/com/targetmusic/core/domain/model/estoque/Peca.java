package com.targetmusic.core.domain.model.estoque;

import com.targetmusic.core.domain.exception.estoque.EstoqueInsuficienteException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Peca {

    private Long id;
    private String nome;
    private String descricao;
    private int quantidadeEstoque;
    private BigDecimal precoUnitario;
    private boolean ativo;
    private Instant createdAt;
    private Instant updatedAt;

    Peca() {
    }

    public static Peca criar(String nome, BigDecimal precoUnitario) {
        Objects.requireNonNull(nome, "nome is required");
        Objects.requireNonNull(precoUnitario, "precoUnitario is required");
        Peca p = new Peca();
        p.nome = nome;
        p.precoUnitario = precoUnitario;
        p.quantidadeEstoque = 0;
        p.ativo = true;
        return p;
    }

    public static Peca fromPersisted(Long id, String nome, String descricao,
                                      int quantidadeEstoque, BigDecimal precoUnitario,
                                      boolean ativo, Instant createdAt, Instant updatedAt) {
        Peca p = new Peca();
        p.id = id;
        p.nome = nome;
        p.descricao = descricao;
        p.quantidadeEstoque = quantidadeEstoque;
        p.precoUnitario = precoUnitario;
        p.ativo = ativo;
        p.createdAt = createdAt;
        p.updatedAt = updatedAt;
        return p;
    }

    public void darEntrada(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("quantidade deve ser maior que zero");
        }
        this.quantidadeEstoque += quantidade;
    }

    public void darSaida(int quantidade) {
        if (this.quantidadeEstoque < quantidade) {
            throw new EstoqueInsuficienteException(this.nome, quantidade, this.quantidadeEstoque);
        }
        this.quantidadeEstoque -= quantidade;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void atualizarPreco(BigDecimal novoPreco) {
        Objects.requireNonNull(novoPreco, "novoPreco is required");
        this.precoUnitario = novoPreco;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public boolean isAtivo() { return ativo; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
