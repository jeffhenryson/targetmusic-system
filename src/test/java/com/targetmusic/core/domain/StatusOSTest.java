package com.targetmusic.core.domain;

import com.targetmusic.core.domain.model.os.StatusOS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class StatusOSTest {

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
        "RECEBIDO,             EM_ANALISE",
        "RECEBIDO,             CANCELADO",
        "EM_ANALISE,           AGUARDANDO_APROVACAO",
        "EM_ANALISE,           CANCELADO",
        "AGUARDANDO_APROVACAO, EM_MANUTENCAO",
        "AGUARDANDO_APROVACAO, CANCELADO",
        "EM_MANUTENCAO,        AGUARDANDO_PECA",
        "EM_MANUTENCAO,        PRONTO",
        "EM_MANUTENCAO,        CANCELADO",
        "AGUARDANDO_PECA,      EM_MANUTENCAO",
        "AGUARDANDO_PECA,      CANCELADO",
        "PRONTO,               ENTREGUE",
        "PRONTO,               EM_MANUTENCAO"
    })
    void valid_transition(StatusOS de, StatusOS para) {
        assertThat(StatusOS.transicaoValida(de, para)).isTrue();
    }

    @ParameterizedTest(name = "{0} → {1} deve ser invalida")
    @CsvSource({
        "ENTREGUE,             RECEBIDO",
        "ENTREGUE,             EM_ANALISE",
        "CANCELADO,            RECEBIDO",
        "CANCELADO,            EM_MANUTENCAO",
        "RECEBIDO,             PRONTO",
        "RECEBIDO,             ENTREGUE",
        "EM_ANALISE,           ENTREGUE",
        "PRONTO,               RECEBIDO",
        "AGUARDANDO_APROVACAO, AGUARDANDO_PECA"
    })
    void invalid_transition(StatusOS de, StatusOS para) {
        assertThat(StatusOS.transicaoValida(de, para)).isFalse();
    }
}
