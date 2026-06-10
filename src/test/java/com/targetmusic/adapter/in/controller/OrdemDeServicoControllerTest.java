package com.targetmusic.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.targetmusic.adapter.in.converter.OrdemDeServicoDTOConverter;
import com.targetmusic.core.domain.exception.os.OrdemDeServicoNotFoundException;
import com.targetmusic.core.domain.exception.os.TransicaoStatusInvalidaException;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;
import com.targetmusic.core.domain.model.os.HistoricoOS;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.domain.model.os.StatusOS;
import com.targetmusic.core.ports.in.ClienteUseCase;
import com.targetmusic.core.ports.in.InstrumentoUseCase;
import com.targetmusic.core.ports.in.OrdemDeServicoUseCase;
import com.targetmusic.infra.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrdemDeServicoControllerTest {

    private MockMvc mockMvc;
    private OrdemDeServicoUseCase osUseCase;
    private ClienteUseCase clienteUseCase;
    private InstrumentoUseCase instrumentoUseCase;
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setup() {
        osUseCase = mock(OrdemDeServicoUseCase.class);
        clienteUseCase = mock(ClienteUseCase.class);
        instrumentoUseCase = mock(InstrumentoUseCase.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new OrdemDeServicoController(osUseCase, clienteUseCase,
                        instrumentoUseCase, new OrdemDeServicoDTOConverter()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private OrdemDeServico osStub() {
        return OrdemDeServico.fromPersisted(1L, "OS-2026-0001", StatusOS.RECEBIDO,
                1L, 1L, "atendente1", Set.of(), "Som falhando",
                null, null, null, null, Instant.now(), null, null, Instant.now(), Instant.now());
    }

    private Cliente clienteStub() {
        return Cliente.fromPersisted(1L, "João Silva", "11999990000",
                null, null, null, null, null, Instant.now());
    }

    private Instrumento instrumentoStub() {
        return Instrumento.fromPersisted(1L, TipoInstrumento.GUITARRA, "Fender", "Stratocaster",
                null, null, null, 1L, Instant.now());
    }

    @Test
    void abrir_retorna_201_com_os() throws Exception {
        when(osUseCase.abrir(any(), any(), any(), any(), any())).thenReturn(osStub());
        when(clienteUseCase.buscarPorId(1L)).thenReturn(clienteStub());
        when(instrumentoUseCase.buscarPorId(1L)).thenReturn(instrumentoStub());

        mockMvc.perform(post("/os")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instrumentoId":1,"clienteId":1,"descricaoProblema":"Som falhando"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("OS-2026-0001"))
                .andExpect(jsonPath("$.status").value("RECEBIDO"));
    }

    @Test
    void abrir_sem_descricaoProblema_retorna_400() throws Exception {
        mockMvc.perform(post("/os")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instrumentoId":1,"clienteId":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_retorna_200_com_pagina() throws Exception {
        OrdemDeServico os = osStub();
        PageResult<OrdemDeServico> page = new PageResult<>(List.of(os), 0, 20, 1L, 1);
        when(osUseCase.listar(null, null, null, 0, 20)).thenReturn(page);
        when(clienteUseCase.buscarPorId(1L)).thenReturn(clienteStub());
        when(instrumentoUseCase.buscarPorId(1L)).thenReturn(instrumentoStub());

        mockMvc.perform(get("/os"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].numero").value("OS-2026-0001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void buscarPorId_retorna_200_quando_existe() throws Exception {
        when(osUseCase.buscarPorId(1L)).thenReturn(osStub());
        when(clienteUseCase.buscarPorId(1L)).thenReturn(clienteStub());
        when(instrumentoUseCase.buscarPorId(1L)).thenReturn(instrumentoStub());

        mockMvc.perform(get("/os/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("OS-2026-0001"))
                .andExpect(jsonPath("$.instrumento.marca").value("Fender"))
                .andExpect(jsonPath("$.cliente.nome").value("João Silva"));
    }

    @Test
    void buscarPorId_retorna_404_quando_nao_existe() throws Exception {
        when(osUseCase.buscarPorId(99L)).thenThrow(new OrdemDeServicoNotFoundException(99L));

        mockMvc.perform(get("/os/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("OS_NOT_FOUND"));
    }

    @Test
    void buscarPorNumero_retorna_200_quando_existe() throws Exception {
        when(osUseCase.buscarPorNumero("OS-2026-0001")).thenReturn(osStub());
        when(clienteUseCase.buscarPorId(1L)).thenReturn(clienteStub());
        when(instrumentoUseCase.buscarPorId(1L)).thenReturn(instrumentoStub());

        mockMvc.perform(get("/os/numero/OS-2026-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("OS-2026-0001"));
    }

    @Test
    void atribuirTecnico_adicionar_retorna_204() throws Exception {
        mockMvc.perform(patch("/os/1/tecnico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tecnicoUsername":"tecnico1","acao":"ADICIONAR"}
                                """))
                .andExpect(status().isNoContent());

        verify(osUseCase).adicionarTecnico(1L, "tecnico1");
    }

    @Test
    void atribuirTecnico_remover_retorna_204() throws Exception {
        mockMvc.perform(patch("/os/1/tecnico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tecnicoUsername":"tecnico1","acao":"REMOVER"}
                                """))
                .andExpect(status().isNoContent());

        verify(osUseCase).removerTecnico(1L, "tecnico1");
    }

    @Test
    void atualizarStatus_retorna_204_para_transicao_valida() throws Exception {
        mockMvc.perform(patch("/os/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"novoStatus":"EM_ANALISE","observacao":"técnico assumiu"}
                                """))
                .andExpect(status().isNoContent());

        verify(osUseCase).atualizarStatus(eq(1L), eq(StatusOS.EM_ANALISE), any(), eq("técnico assumiu"));
    }

    @Test
    void atualizarStatus_retorna_422_para_transicao_invalida() throws Exception {
        doThrow(new TransicaoStatusInvalidaException(StatusOS.RECEBIDO, StatusOS.PRONTO))
                .when(osUseCase).atualizarStatus(any(), any(), any(), any());

        mockMvc.perform(patch("/os/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"novoStatus":"PRONTO"}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value("TRANSICAO_STATUS_INVALIDA"));
    }

    @Test
    void buscarHistorico_retorna_200_com_lista() throws Exception {
        HistoricoOS h = new HistoricoOS(1L, 1L, null, StatusOS.RECEBIDO, "atendente1", null, Instant.now());
        when(osUseCase.buscarHistorico(1L)).thenReturn(List.of(h));

        mockMvc.perform(get("/os/1/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statusNovo").value("RECEBIDO"))
                .andExpect(jsonPath("$[0].usuarioUsername").value("atendente1"));
    }
}
