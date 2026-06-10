package com.targetmusic.core.service;

import com.targetmusic.core.domain.exception.cliente.ClienteNotFoundException;
import com.targetmusic.core.domain.exception.cliente.ClienteTemOSEmAbertoException;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.ports.out.cliente.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock ClienteRepository clienteRepository;

    ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteService(clienteRepository);
    }

    @Test
    void criar_salva_e_retorna_cliente() {
        Cliente salvo = Cliente.fromPersisted(1L, "João Silva", "11999990000",
                "joao@email.com", "12345678901", null, null, null, Instant.now());
        when(clienteRepository.save(any())).thenReturn(salvo);

        Cliente result = clienteService.criar("João Silva", "11999990000", "joao@email.com",
                "12345678901", null, null);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("João Silva");
        verify(clienteRepository).save(any());
    }

    @Test
    void criar_com_nome_nulo_lanca_excecao() {
        assertThatThrownBy(() -> clienteService.criar(null, "11999990000", null, null, null, null))
                .isInstanceOf(NullPointerException.class);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void buscarPorId_retorna_cliente_quando_existe() {
        Cliente c = Cliente.fromPersisted(1L, "Maria", "11888880000", null, null, null, null, null, Instant.now());
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(c));

        Cliente result = clienteService.buscarPorId(1L);

        assertThat(result.getNome()).isEqualTo("Maria");
    }

    @Test
    void buscarPorId_lanca_ClienteNotFoundException_quando_nao_existe() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(ClienteNotFoundException.class);
    }

    @Test
    void listar_delega_ao_repositorio() {
        PageResult<Cliente> page = new PageResult<>(List.of(), 0, 10, 0L, 0);
        when(clienteRepository.findAll(null, 0, 10)).thenReturn(page);

        PageResult<Cliente> result = clienteService.listar(null, 0, 10);

        assertThat(result.totalElements()).isEqualTo(0L);
        verify(clienteRepository).findAll(null, 0, 10);
    }

    @Test
    void atualizar_atualiza_campos_e_retorna() {
        Cliente existente = Cliente.fromPersisted(1L, "Ana", "11777770000", null, null, null, null, null, Instant.now());
        Cliente atualizado = Cliente.fromPersisted(1L, "Ana Lima", "11777770001", "ana@email.com",
                null, null, null, null, Instant.now());
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any())).thenReturn(atualizado);

        Cliente result = clienteService.atualizar(1L, "Ana Lima", "11777770001", "ana@email.com",
                null, null, null);

        assertThat(result.getNome()).isEqualTo("Ana Lima");
        verify(clienteRepository).save(any());
    }

    @Test
    void atualizar_lanca_ClienteNotFoundException_quando_nao_existe() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizar(99L, "X", "11900000000", null, null, null, null))
                .isInstanceOf(ClienteNotFoundException.class);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void remover_deleta_quando_nao_tem_os_em_aberto() {
        Cliente c = Cliente.fromPersisted(1L, "Carlos", "11666660000", null, null, null, null, null, Instant.now());
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(c));
        when(clienteRepository.hasOpenOS(1L)).thenReturn(false);

        clienteService.remover(1L);

        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void remover_lanca_ClienteTemOSEmAbertoException_quando_tem_os_em_aberto() {
        Cliente c = Cliente.fromPersisted(1L, "Carlos", "11666660000", null, null, null, null, null, Instant.now());
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(c));
        when(clienteRepository.hasOpenOS(1L)).thenReturn(true);

        assertThatThrownBy(() -> clienteService.remover(1L))
                .isInstanceOf(ClienteTemOSEmAbertoException.class);
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    void remover_lanca_ClienteNotFoundException_quando_nao_existe() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.remover(99L))
                .isInstanceOf(ClienteNotFoundException.class);
    }

    @Test
    void vincularUsuario_vincula_e_salva() {
        Cliente c = Cliente.fromPersisted(1L, "Pedro", "11555550000", null, null, null, null, null, Instant.now());
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(c));
        when(clienteRepository.save(any())).thenReturn(c);

        clienteService.vincularUsuario(1L, 42L);

        verify(clienteRepository).save(any());
    }
}
