package com.targetmusic.core.service;

import com.targetmusic.core.domain.exception.cliente.ClienteNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoTemOSEmAbertoException;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;
import com.targetmusic.core.ports.out.cliente.ClienteRepository;
import com.targetmusic.core.ports.out.instrumento.InstrumentoRepository;
import com.targetmusic.core.domain.model.cliente.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.targetmusic.core.domain.model.PageResult;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstrumentoServiceTest {

    @Mock InstrumentoRepository instrumentoRepository;
    @Mock ClienteRepository clienteRepository;

    InstrumentoService instrumentoService;

    @BeforeEach
    void setUp() {
        instrumentoService = new InstrumentoService(instrumentoRepository, clienteRepository);
    }

    private Cliente clienteStub(Long id) {
        return Cliente.fromPersisted(id, "Cliente", "11999990000", null, null, null, null, null, Instant.now());
    }

    private Instrumento instrumentoStub(Long id) {
        return Instrumento.fromPersisted(id, TipoInstrumento.GUITARRA, "Fender", "Stratocaster",
                "SN001", "Sunburst", null, 1L, Instant.now());
    }

    @Test
    void criar_valida_cliente_e_salva() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteStub(1L)));
        when(instrumentoRepository.save(any())).thenReturn(instrumentoStub(1L));

        Instrumento result = instrumentoService.criar(TipoInstrumento.GUITARRA, "Fender", "Stratocaster",
                1L, "SN001", "Sunburst", null);

        assertThat(result.getId()).isEqualTo(1L);
        verify(instrumentoRepository).save(any());
    }

    @Test
    void criar_lanca_ClienteNotFoundException_quando_cliente_inexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instrumentoService.criar(TipoInstrumento.GUITARRA, "Fender",
                "Stratocaster", 99L, null, null, null))
                .isInstanceOf(ClienteNotFoundException.class);
        verify(instrumentoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_retorna_instrumento_quando_existe() {
        when(instrumentoRepository.findById(1L)).thenReturn(Optional.of(instrumentoStub(1L)));

        Instrumento result = instrumentoService.buscarPorId(1L);

        assertThat(result.getMarca()).isEqualTo("Fender");
    }

    @Test
    void buscarPorId_lanca_InstrumentoNotFoundException_quando_nao_existe() {
        when(instrumentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instrumentoService.buscarPorId(99L))
                .isInstanceOf(InstrumentoNotFoundException.class);
    }

    @Test
    void listarPorCliente_valida_cliente_e_delega_ao_repositorio() {
        PageResult<Instrumento> page = new PageResult<>(List.of(instrumentoStub(1L)), 0, 20, 1L, 1);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteStub(1L)));
        when(instrumentoRepository.findByClienteId(1L, 0, 20)).thenReturn(page);

        PageResult<Instrumento> result = instrumentoService.listarPorCliente(1L, 0, 20);

        assertThat(result.content()).hasSize(1);
        verify(instrumentoRepository).findByClienteId(1L, 0, 20);
    }

    @Test
    void listarPorCliente_lanca_ClienteNotFoundException_quando_cliente_inexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instrumentoService.listarPorCliente(99L, 0, 20))
                .isInstanceOf(ClienteNotFoundException.class);
    }

    @Test
    void atualizar_atualiza_campos_e_retorna() {
        Instrumento existente = instrumentoStub(1L);
        Instrumento atualizado = Instrumento.fromPersisted(1L, TipoInstrumento.BAIXO, "Gibson", "Les Paul",
                null, "Black", null, 1L, Instant.now());
        when(instrumentoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(instrumentoRepository.save(any())).thenReturn(atualizado);

        Instrumento result = instrumentoService.atualizar(1L, TipoInstrumento.BAIXO, "Gibson", "Les Paul",
                null, "Black", null);

        assertThat(result.getMarca()).isEqualTo("Gibson");
        verify(instrumentoRepository).save(any());
    }

    @Test
    void atualizar_lanca_InstrumentoNotFoundException_quando_nao_existe() {
        when(instrumentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instrumentoService.atualizar(99L, TipoInstrumento.GUITARRA,
                "X", "Y", null, null, null))
                .isInstanceOf(InstrumentoNotFoundException.class);
        verify(instrumentoRepository, never()).save(any());
    }

    @Test
    void remover_deleta_quando_nao_tem_os_em_aberto() {
        when(instrumentoRepository.findById(1L)).thenReturn(Optional.of(instrumentoStub(1L)));
        when(instrumentoRepository.hasOpenOS(1L)).thenReturn(false);

        instrumentoService.remover(1L);

        verify(instrumentoRepository).deleteById(1L);
    }

    @Test
    void remover_lanca_InstrumentoTemOSEmAbertoException_quando_tem_os_em_aberto() {
        when(instrumentoRepository.findById(1L)).thenReturn(Optional.of(instrumentoStub(1L)));
        when(instrumentoRepository.hasOpenOS(1L)).thenReturn(true);

        assertThatThrownBy(() -> instrumentoService.remover(1L))
                .isInstanceOf(InstrumentoTemOSEmAbertoException.class);
        verify(instrumentoRepository, never()).deleteById(any());
    }

    @Test
    void buscarPorIds_retorna_mapa_indexado_por_id() {
        Instrumento i1 = instrumentoStub(1L);
        Instrumento i2 = Instrumento.fromPersisted(2L, TipoInstrumento.BAIXO, "Gibson", "SG",
                "SN002", "Black", null, 1L, Instant.now());
        when(instrumentoRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(List.of(i1, i2));

        Map<Long, Instrumento> result = instrumentoService.buscarPorIds(List.of(1L, 2L));

        assertThat(result).containsKeys(1L, 2L);
        assertThat(result.get(1L).getMarca()).isEqualTo("Fender");
        assertThat(result.get(2L).getMarca()).isEqualTo("Gibson");
    }

    @Test
    void remover_lanca_InstrumentoNotFoundException_quando_nao_existe() {
        when(instrumentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instrumentoService.remover(99L))
                .isInstanceOf(InstrumentoNotFoundException.class);
    }
}
