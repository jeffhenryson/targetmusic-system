package com.targetmusic.core.domain;

import com.targetmusic.core.domain.exception.estoque.EstoqueInsuficienteException;
import com.targetmusic.core.domain.model.estoque.Peca;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PecaTest {

    // ── criação ───────────────────────────────────────────────────────────────

    @Test
    void criar_peca_fica_ativa_com_estoque_zero() {
        Peca peca = Peca.criar("Palheta", new BigDecimal("2.50"));
        assertThat(peca.isAtivo()).isTrue();
        assertThat(peca.getQuantidadeEstoque()).isZero();
        assertThat(peca.getPrecoUnitario()).isEqualByComparingTo("2.50");
    }

    @Test
    void criar_nome_nulo_lanca_excecao() {
        assertThatThrownBy(() -> Peca.criar(null, new BigDecimal("10.00")))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void criar_preco_nulo_lanca_excecao() {
        assertThatThrownBy(() -> Peca.criar("Palheta", null))
                .isInstanceOf(NullPointerException.class);
    }

    // ── darEntrada ────────────────────────────────────────────────────────────

    @Test
    void darEntrada_aumenta_estoque() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        peca.darEntrada(5);
        assertThat(peca.getQuantidadeEstoque()).isEqualTo(5);
    }

    @Test
    void darEntrada_acumula_multiplas_entradas() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        peca.darEntrada(3);
        peca.darEntrada(7);
        assertThat(peca.getQuantidadeEstoque()).isEqualTo(10);
    }

    @Test
    void darEntrada_quantidade_zero_lanca_excecao() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        assertThatThrownBy(() -> peca.darEntrada(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void darEntrada_quantidade_negativa_lanca_excecao() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        assertThatThrownBy(() -> peca.darEntrada(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── darSaida ──────────────────────────────────────────────────────────────

    @Test
    void darSaida_reduz_estoque() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        peca.darEntrada(5);
        peca.darSaida(2);
        assertThat(peca.getQuantidadeEstoque()).isEqualTo(3);
    }

    @Test
    void darSaida_estoque_exato_vai_a_zero() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        peca.darEntrada(3);
        peca.darSaida(3);
        assertThat(peca.getQuantidadeEstoque()).isZero();
    }

    @Test
    void darSaida_estoque_insuficiente_lanca_EstoqueInsuficienteException() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        peca.darEntrada(2);
        assertThatThrownBy(() -> peca.darSaida(3))
                .isInstanceOf(EstoqueInsuficienteException.class);
    }

    @Test
    void darSaida_estoque_zero_lanca_EstoqueInsuficienteException() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        assertThatThrownBy(() -> peca.darSaida(1))
                .isInstanceOf(EstoqueInsuficienteException.class);
    }

    // ── desativar ─────────────────────────────────────────────────────────────

    @Test
    void desativar_marca_peca_como_inativa() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        peca.desativar();
        assertThat(peca.isAtivo()).isFalse();
    }

    // ── atualizarPreco ────────────────────────────────────────────────────────

    @Test
    void atualizarPreco_substitui_valor() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        peca.atualizarPreco(new BigDecimal("18.00"));
        assertThat(peca.getPrecoUnitario()).isEqualByComparingTo("18.00");
    }

    @Test
    void atualizarPreco_nulo_lanca_excecao() {
        Peca peca = Peca.criar("Corda", new BigDecimal("15.00"));
        assertThatThrownBy(() -> peca.atualizarPreco(null))
                .isInstanceOf(NullPointerException.class);
    }
}
