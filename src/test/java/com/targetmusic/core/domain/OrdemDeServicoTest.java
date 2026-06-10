package com.targetmusic.core.domain;

import com.targetmusic.core.domain.exception.os.TransicaoStatusInvalidaException;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.domain.model.os.StatusOS;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrdemDeServicoTest {

    // ── abrir ─────────────────────────────────────────────────────────────────

    @Test
    void abrir_cria_os_com_status_recebido() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Som falhando");

        assertThat(os.getStatus()).isEqualTo(StatusOS.RECEBIDO);
        assertThat(os.getNumero()).isEqualTo("OS-2026-00001");
        assertThat(os.getAtendenteUsername()).isEqualTo("atendente1");
        assertThat(os.getDescricaoProblema()).isEqualTo("Som falhando");
        assertThat(os.getTecnicosUsernames()).isEmpty();
        assertThat(os.getDataRecebimento()).isNotNull();
        assertThat(os.getDataEntrega()).isNull();
    }

    @Test
    void abrir_numero_nulo_lanca_excecao() {
        assertThatThrownBy(() -> OrdemDeServico.abrir(null, 1L, 1L, "atendente1", "Problema"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void abrir_atendente_nulo_lanca_excecao() {
        assertThatThrownBy(() -> OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, null, "Problema"))
                .isInstanceOf(NullPointerException.class);
    }

    // ── mudarStatus ───────────────────────────────────────────────────────────

    @Test
    void mudarStatus_transicao_valida_atualiza_status() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");

        os.mudarStatus(StatusOS.EM_ANALISE);

        assertThat(os.getStatus()).isEqualTo(StatusOS.EM_ANALISE);
    }

    @Test
    void mudarStatus_transicao_invalida_lanca_TransicaoStatusInvalidaException() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");

        assertThatThrownBy(() -> os.mudarStatus(StatusOS.PRONTO))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
    }

    @Test
    void mudarStatus_status_nulo_lanca_excecao() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");

        assertThatThrownBy(() -> os.mudarStatus(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void mudarStatus_estado_final_entregue_nao_permite_nova_transicao() {
        OrdemDeServico os = emStatus(StatusOS.PRONTO);
        os.mudarStatus(StatusOS.ENTREGUE);

        assertThatThrownBy(() -> os.mudarStatus(StatusOS.EM_MANUTENCAO))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
    }

    @Test
    void mudarStatus_estado_final_cancelado_nao_permite_nova_transicao() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");
        os.mudarStatus(StatusOS.CANCELADO);

        assertThatThrownBy(() -> os.mudarStatus(StatusOS.EM_ANALISE))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
    }

    // ── adicionarTecnico / removerTecnico ─────────────────────────────────────

    @Test
    void adicionarTecnico_inclui_username_no_set() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");

        os.adicionarTecnico("tecnico1");

        assertThat(os.getTecnicosUsernames()).containsExactly("tecnico1");
    }

    @Test
    void adicionarTecnico_mesmo_username_nao_duplica() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");

        os.adicionarTecnico("tecnico1");
        os.adicionarTecnico("tecnico1");

        assertThat(os.getTecnicosUsernames()).hasSize(1);
    }

    @Test
    void adicionarTecnico_multiplos_tecnicos() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");

        os.adicionarTecnico("tecnico1");
        os.adicionarTecnico("tecnico2");

        assertThat(os.getTecnicosUsernames()).containsExactlyInAnyOrder("tecnico1", "tecnico2");
    }

    @Test
    void removerTecnico_remove_username_do_set() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");
        os.adicionarTecnico("tecnico1");

        os.removerTecnico("tecnico1");

        assertThat(os.getTecnicosUsernames()).isEmpty();
    }

    @Test
    void removerTecnico_username_ausente_e_noop() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");
        os.adicionarTecnico("tecnico1");

        os.removerTecnico("tecnico_inexistente");

        assertThat(os.getTecnicosUsernames()).hasSize(1);
    }

    // ── registrarEntrega ──────────────────────────────────────────────────────

    @Test
    void registrarEntrega_a_partir_de_pronto_define_data_e_muda_status() {
        OrdemDeServico os = emStatus(StatusOS.PRONTO);

        os.registrarEntrega();

        assertThat(os.getStatus()).isEqualTo(StatusOS.ENTREGUE);
        assertThat(os.getDataEntrega()).isNotNull();
    }

    @Test
    void registrarEntrega_quando_nao_esta_pronto_lanca_TransicaoStatusInvalidaException() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");

        assertThatThrownBy(() -> os.registrarEntrega())
                .isInstanceOf(TransicaoStatusInvalidaException.class);
    }

    @Test
    void getTecnicosUsernames_retorna_copia_defensiva() {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");
        os.adicionarTecnico("tecnico1");

        os.getTecnicosUsernames().add("intruso");

        assertThat(os.getTecnicosUsernames()).containsExactly("tecnico1");
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private OrdemDeServico emStatus(StatusOS alvo) {
        OrdemDeServico os = OrdemDeServico.abrir("OS-2026-00001", 1L, 1L, "atendente1", "Problema");
        switch (alvo) {
            case EM_ANALISE           -> os.mudarStatus(StatusOS.EM_ANALISE);
            case AGUARDANDO_APROVACAO -> { os.mudarStatus(StatusOS.EM_ANALISE);
                                           os.mudarStatus(StatusOS.AGUARDANDO_APROVACAO); }
            case EM_MANUTENCAO        -> { os.mudarStatus(StatusOS.EM_ANALISE);
                                           os.mudarStatus(StatusOS.AGUARDANDO_APROVACAO);
                                           os.mudarStatus(StatusOS.EM_MANUTENCAO); }
            case AGUARDANDO_PECA      -> { os.mudarStatus(StatusOS.EM_ANALISE);
                                           os.mudarStatus(StatusOS.AGUARDANDO_APROVACAO);
                                           os.mudarStatus(StatusOS.EM_MANUTENCAO);
                                           os.mudarStatus(StatusOS.AGUARDANDO_PECA); }
            case PRONTO               -> { os.mudarStatus(StatusOS.EM_ANALISE);
                                           os.mudarStatus(StatusOS.AGUARDANDO_APROVACAO);
                                           os.mudarStatus(StatusOS.EM_MANUTENCAO);
                                           os.mudarStatus(StatusOS.PRONTO); }
            case CANCELADO            -> os.mudarStatus(StatusOS.CANCELADO);
            default -> {}
        }
        return os;
    }
}
