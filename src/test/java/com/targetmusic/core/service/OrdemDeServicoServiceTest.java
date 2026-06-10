package com.targetmusic.core.service;

import com.targetmusic.core.domain.exception.cliente.ClienteNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoNotFoundException;
import com.targetmusic.core.domain.exception.os.OSNaoPodeSerRemovidaException;
import com.targetmusic.core.domain.exception.os.OrdemDeServicoNotFoundException;
import com.targetmusic.core.domain.exception.os.TransicaoStatusInvalidaException;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;
import com.targetmusic.core.domain.model.os.HistoricoOS;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.domain.model.os.StatusOS;
import com.targetmusic.core.ports.out.cliente.ClienteRepository;
import com.targetmusic.core.ports.out.instrumento.InstrumentoRepository;
import com.targetmusic.core.ports.out.os.HistoricoOSRepository;
import com.targetmusic.core.ports.out.os.OSNumeroSequencePort;
import com.targetmusic.core.ports.out.os.OrdemDeServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemDeServicoServiceTest {

    @Mock OrdemDeServicoRepository osRepository;
    @Mock HistoricoOSRepository historicoRepository;
    @Mock OSNumeroSequencePort sequencePort;
    @Mock ClienteRepository clienteRepository;
    @Mock InstrumentoRepository instrumentoRepository;

    OrdemDeServicoService service;

    @BeforeEach
    void setUp() {
        service = new OrdemDeServicoService(osRepository, historicoRepository, sequencePort,
                clienteRepository, instrumentoRepository);
    }

    private Cliente clienteStub() {
        return Cliente.fromPersisted(1L, "João Silva", "11999990000", null, null, null, null, null, Instant.now());
    }

    private Instrumento instrumentoStub() {
        return Instrumento.fromPersisted(1L, TipoInstrumento.GUITARRA, "Fender", "Stratocaster",
                null, null, null, 1L, Instant.now());
    }

    private OrdemDeServico osRecebida() {
        return OrdemDeServico.fromPersisted(1L, "OS-2026-0001", StatusOS.RECEBIDO,
                1L, 1L, "atendente1", Set.of(), "Som falhando",
                null, null, null, null, Instant.now(), null, null, Instant.now(), Instant.now());
    }

    @Test
    void abrir_gera_numero_e_salva_os_com_historico_inicial() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteStub()));
        when(instrumentoRepository.findById(1L)).thenReturn(Optional.of(instrumentoStub()));
        when(sequencePort.nextNumero()).thenReturn("OS-2026-0001");
        OrdemDeServico salva = osRecebida();
        when(osRepository.save(any())).thenReturn(salva);

        OrdemDeServico result = service.abrir(1L, 1L, "atendente1", "Som falhando", null);

        assertThat(result.getNumero()).isEqualTo("OS-2026-0001");
        assertThat(result.getStatus()).isEqualTo(StatusOS.RECEBIDO);
        verify(osRepository).save(any());
        verify(historicoRepository).save(any());
    }

    @Test
    void abrir_lanca_ClienteNotFoundException_quando_cliente_nao_existe() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.abrir(1L, 99L, "atendente1", "problema", null))
                .isInstanceOf(ClienteNotFoundException.class);
        verify(osRepository, never()).save(any());
    }

    @Test
    void abrir_lanca_InstrumentoNotFoundException_quando_instrumento_nao_existe() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteStub()));
        when(instrumentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.abrir(99L, 1L, "atendente1", "problema", null))
                .isInstanceOf(InstrumentoNotFoundException.class);
        verify(osRepository, never()).save(any());
    }

    @Test
    void buscarPorId_retorna_os_quando_existe() {
        when(osRepository.findById(1L)).thenReturn(Optional.of(osRecebida()));

        OrdemDeServico result = service.buscarPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_lanca_OrdemDeServicoNotFoundException_quando_nao_existe() {
        when(osRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }

    @Test
    void buscarPorNumero_retorna_os_quando_existe() {
        when(osRepository.findByNumero("OS-2026-0001")).thenReturn(Optional.of(osRecebida()));

        OrdemDeServico result = service.buscarPorNumero("OS-2026-0001");

        assertThat(result.getNumero()).isEqualTo("OS-2026-0001");
    }

    @Test
    void buscarPorNumero_lanca_exception_quando_nao_existe() {
        when(osRepository.findByNumero("OS-2099-9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorNumero("OS-2099-9999"))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }

    @Test
    void listar_delega_ao_repositorio() {
        PageResult<OrdemDeServico> page = new PageResult<>(List.of(), 0, 20, 0L, 0);
        when(osRepository.findAll(null, null, null, 0, 20)).thenReturn(page);

        PageResult<OrdemDeServico> result = service.listar(null, null, null, 0, 20);

        assertThat(result.totalElements()).isEqualTo(0L);
        verify(osRepository).findAll(null, null, null, 0, 20);
    }

    @Test
    void listarPorCliente_valida_cliente_e_delega() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteStub()));
        when(osRepository.findByClienteId(1L)).thenReturn(List.of(osRecebida()));

        List<OrdemDeServico> result = service.listarPorCliente(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void listarPorCliente_lanca_exception_quando_cliente_nao_existe() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listarPorCliente(99L))
                .isInstanceOf(ClienteNotFoundException.class);
    }

    @Test
    void adicionarTecnico_adiciona_e_salva() {
        OrdemDeServico os = osRecebida();
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(os);

        service.adicionarTecnico(1L, "tecnico1");

        verify(osRepository).save(any());
    }

    @Test
    void adicionarTecnico_lanca_exception_quando_os_nao_existe() {
        when(osRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.adicionarTecnico(99L, "tecnico1"))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }

    @Test
    void removerTecnico_remove_e_salva() {
        OrdemDeServico os = osRecebida();
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(os);

        service.removerTecnico(1L, "tecnico1");

        verify(osRepository).save(any());
    }

    @Test
    void atualizarStatus_transicao_valida_muda_status_e_salva_historico() {
        OrdemDeServico os = osRecebida(); // RECEBIDO
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(os);

        service.atualizarStatus(1L, StatusOS.EM_ANALISE, "tecnico1", null);

        verify(osRepository).save(any());
        verify(historicoRepository).save(any());
    }

    @Test
    void atualizarStatus_transicao_invalida_lanca_TransicaoStatusInvalidaException() {
        OrdemDeServico os = osRecebida(); // RECEBIDO
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));

        assertThatThrownBy(() -> service.atualizarStatus(1L, StatusOS.PRONTO, "tecnico1", null))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
        verify(osRepository, never()).save(any());
        verify(historicoRepository, never()).save(any());
    }

    @Test
    void atualizarStatus_lanca_exception_quando_os_nao_existe() {
        when(osRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarStatus(99L, StatusOS.EM_ANALISE, "u", null))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }

    @Test
    void buscarHistorico_retorna_lista_quando_os_existe() {
        when(osRepository.findById(1L)).thenReturn(Optional.of(osRecebida()));
        HistoricoOS h = new HistoricoOS(1L, 1L, null, StatusOS.RECEBIDO, "atendente1", null, Instant.now());
        when(historicoRepository.findByOsId(1L)).thenReturn(List.of(h));

        List<HistoricoOS> result = service.buscarHistorico(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).statusNovo()).isEqualTo(StatusOS.RECEBIDO);
    }

    @Test
    void buscarHistorico_lanca_exception_quando_os_nao_existe() {
        when(osRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarHistorico(99L))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }

    // ── helpers Sprint D ───────────────────────────────────────────────────────

    private OrdemDeServico osEmAnalise() {
        return OrdemDeServico.fromPersisted(1L, "OS-2026-0001", StatusOS.EM_ANALISE,
                1L, 1L, "atendente1", Set.of("tecnico1"), "Som falhando",
                null, null, null, null, Instant.now(), null, null, Instant.now(), Instant.now());
    }

    private OrdemDeServico osAguardandoAprovacao() {
        return OrdemDeServico.fromPersisted(1L, "OS-2026-0001", StatusOS.AGUARDANDO_APROVACAO,
                1L, 1L, "atendente1", Set.of("tecnico1"), "Som falhando",
                null, new BigDecimal("250.00"), null, null, Instant.now(), null, null, Instant.now(), Instant.now());
    }

    private OrdemDeServico osPronta() {
        return OrdemDeServico.fromPersisted(1L, "OS-2026-0001", StatusOS.PRONTO,
                1L, 1L, "atendente1", Set.of("tecnico1"), "Som falhando",
                "Potenciômetro trocado", new BigDecimal("250.00"), null, null, Instant.now(), null, null, Instant.now(), Instant.now());
    }

    private OrdemDeServico osCancelada() {
        return OrdemDeServico.fromPersisted(1L, "OS-2026-0001", StatusOS.CANCELADO,
                1L, 1L, "atendente1", Set.of(), "Som falhando",
                null, null, null, null, Instant.now(), null, null, Instant.now(), Instant.now());
    }

    // ── definirOrcamento ───────────────────────────────────────────────────────

    @Test
    void definirOrcamento_muda_status_para_aguardando_e_salva_historico() {
        OrdemDeServico os = osEmAnalise();
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(os);

        service.definirOrcamento(1L, new BigDecimal("250.00"), LocalDate.of(2026, 7, 1), "tecnico1");

        verify(osRepository).save(any());
        verify(historicoRepository).save(any());
    }

    @Test
    void definirOrcamento_transicao_invalida_lanca_excecao() {
        when(osRepository.findById(1L)).thenReturn(Optional.of(osRecebida())); // RECEBIDO → inválido

        assertThatThrownBy(() -> service.definirOrcamento(1L, new BigDecimal("100"), null, "tecnico1"))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
        verify(osRepository, never()).save(any());
    }

    @Test
    void definirOrcamento_lanca_exception_quando_os_nao_existe() {
        when(osRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.definirOrcamento(99L, new BigDecimal("100"), null, "u"))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }

    // ── aprovarOrcamento ───────────────────────────────────────────────────────

    @Test
    void aprovarOrcamento_muda_para_em_manutencao_e_salva_historico() {
        OrdemDeServico os = osAguardandoAprovacao();
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(os);

        service.aprovarOrcamento(1L, "atendente1");

        verify(osRepository).save(any());
        verify(historicoRepository).save(any());
    }

    @Test
    void aprovarOrcamento_transicao_invalida_lanca_excecao() {
        when(osRepository.findById(1L)).thenReturn(Optional.of(osRecebida())); // RECEBIDO → inválido

        assertThatThrownBy(() -> service.aprovarOrcamento(1L, "atendente1"))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
        verify(osRepository, never()).save(any());
    }

    @Test
    void aprovarOrcamento_lanca_exception_quando_os_nao_existe() {
        when(osRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.aprovarOrcamento(99L, "u"))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }

    // ── recusarOrcamento ───────────────────────────────────────────────────────

    @Test
    void recusarOrcamento_muda_para_cancelado_e_salva_historico() {
        OrdemDeServico os = osAguardandoAprovacao();
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(os);

        service.recusarOrcamento(1L, "Muito caro", "atendente1");

        verify(osRepository).save(any());
        verify(historicoRepository).save(any());
    }

    @Test
    void recusarOrcamento_usa_observacao_padrao_quando_nula() {
        OrdemDeServico os = osAguardandoAprovacao();
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(os);

        service.recusarOrcamento(1L, null, "atendente1");

        verify(osRepository).save(any());
    }

    // ── registrarEntrega ───────────────────────────────────────────────────────

    @Test
    void registrarEntrega_com_valorFinal_registra_e_salva_historico() {
        OrdemDeServico os = osPronta();
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(os);

        service.registrarEntrega(1L, new BigDecimal("230.00"), "atendente1");

        verify(osRepository).save(any());
        verify(historicoRepository).save(any());
    }

    @Test
    void registrarEntrega_sem_valorFinal_registra_sem_erro() {
        OrdemDeServico os = osPronta();
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(os);

        service.registrarEntrega(1L, null, "atendente1");

        verify(osRepository).save(any());
    }

    @Test
    void registrarEntrega_transicao_invalida_lanca_excecao() {
        when(osRepository.findById(1L)).thenReturn(Optional.of(osRecebida())); // RECEBIDO → inválido

        assertThatThrownBy(() -> service.registrarEntrega(1L, null, "atendente1"))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
        verify(osRepository, never()).save(any());
    }

    @Test
    void registrarEntrega_lanca_exception_quando_os_nao_existe() {
        when(osRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrarEntrega(99L, null, "u"))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }

    // ── atualizar ─────────────────────────────────────────────────────────────

    @Test
    void atualizar_atualiza_campos_editaveis_e_retorna() {
        OrdemDeServico os = osEmAnalise();
        OrdemDeServico atualizada = osEmAnalise();
        when(osRepository.findById(1L)).thenReturn(Optional.of(os));
        when(osRepository.save(any())).thenReturn(atualizada);

        OrdemDeServico result = service.atualizar(1L, "Diagnóstico completo",
                LocalDate.of(2026, 7, 10), "Urgente");

        assertThat(result).isNotNull();
        verify(osRepository).save(any());
    }

    @Test
    void atualizar_lanca_exception_quando_os_nao_existe() {
        when(osRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(99L, null, null, null))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }

    // ── remover ───────────────────────────────────────────────────────────────

    @Test
    void remover_deleta_quando_status_cancelado() {
        when(osRepository.findById(1L)).thenReturn(Optional.of(osCancelada()));

        service.remover(1L);

        verify(osRepository).deleteById(1L);
    }

    @Test
    void remover_deleta_quando_status_recebido() {
        when(osRepository.findById(1L)).thenReturn(Optional.of(osRecebida()));

        service.remover(1L);

        verify(osRepository).deleteById(1L);
    }

    @Test
    void remover_lanca_OSNaoPodeSerRemovidaException_quando_em_analise() {
        when(osRepository.findById(1L)).thenReturn(Optional.of(osEmAnalise()));

        assertThatThrownBy(() -> service.remover(1L))
                .isInstanceOf(OSNaoPodeSerRemovidaException.class);
        verify(osRepository, never()).deleteById(any());
    }

    @Test
    void remover_lanca_exception_quando_os_nao_existe() {
        when(osRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remover(99L))
                .isInstanceOf(OrdemDeServicoNotFoundException.class);
    }
}
