package com.targetmusic.core.domain.model.cliente;

import java.time.Instant;
import java.util.Objects;

public class Cliente {

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String cpf;
    private String endereco;
    private String observacoes;
    private Long userId;
    private Instant createdAt;

    Cliente() {
    }

    public static Cliente of(String nome, String telefone) {
        Objects.requireNonNull(nome, "nome is required");
        Objects.requireNonNull(telefone, "telefone is required");
        Cliente c = new Cliente();
        c.nome = nome;
        c.telefone = telefone;
        return c;
    }

    public static Cliente fromPersisted(Long id, String nome, String telefone, String email,
                                         String cpf, String endereco, String observacoes,
                                         Long userId, Instant createdAt) {
        Cliente c = new Cliente();
        c.id = id;
        c.nome = nome;
        c.telefone = telefone;
        c.email = email;
        c.cpf = cpf;
        c.endereco = endereco;
        c.observacoes = observacoes;
        c.userId = userId;
        c.createdAt = createdAt;
        return c;
    }

    public void vincularUsuario(Long userId) {
        this.userId = userId;
    }

    public void atualizar(String nome, String telefone, String email, String cpf,
                          String endereco, String observacoes) {
        Objects.requireNonNull(nome, "nome is required");
        Objects.requireNonNull(telefone, "telefone is required");
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.cpf = cpf;
        this.endereco = endereco;
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }
    public String getObservacoes() { return observacoes; }
    public Long getUserId() { return userId; }
    public Instant getCreatedAt() { return createdAt; }
}
