package com.targetmusic.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.targetmusic.adapter.in.converter.ClienteDTOConverter;
import com.targetmusic.adapter.in.converter.InstrumentoDTOConverter;
import com.targetmusic.core.domain.exception.cliente.ClienteNotFoundException;
import com.targetmusic.core.domain.exception.cliente.ClienteTemOSEmAbertoException;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;
import com.targetmusic.core.ports.in.ClienteUseCase;
import com.targetmusic.core.ports.in.InstrumentoUseCase;
import com.targetmusic.infra.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ClienteControllerTest {

    private MockMvc mockMvc;
    private ClienteUseCase clienteUseCase;
    private InstrumentoUseCase instrumentoUseCase;
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setup() {
        clienteUseCase = mock(ClienteUseCase.class);
        instrumentoUseCase = mock(InstrumentoUseCase.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ClienteController(clienteUseCase, instrumentoUseCase,
                        new ClienteDTOConverter(), new InstrumentoDTOConverter()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Cliente clienteStub() {
        return Cliente.fromPersisted(1L, "João Silva", "11999990000",
                "joao@email.com", "12345678901", null, null, null, Instant.now());
    }

    @Test
    void criar_retorna_201_com_cliente() throws Exception {
        when(clienteUseCase.criar(any(), any(), any(), any(), any(), any())).thenReturn(clienteStub());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"João Silva","telefone":"11999990000","email":"joao@email.com","cpf":"12345678901"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void criar_sem_nome_retorna_400() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"telefone":"11999990000"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_retorna_200_quando_existe() throws Exception {
        when(clienteUseCase.buscarPorId(1L)).thenReturn(clienteStub());

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void buscarPorId_retorna_404_quando_nao_existe() throws Exception {
        when(clienteUseCase.buscarPorId(99L)).thenThrow(new ClienteNotFoundException(99L));

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void listar_retorna_200_com_pagina() throws Exception {
        PageResult<Cliente> page = new PageResult<>(List.of(clienteStub()), 0, 20, 1L, 1);
        when(clienteUseCase.listar(null, 0, 20)).thenReturn(page);

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("João Silva"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void atualizar_retorna_200_com_cliente_atualizado() throws Exception {
        Cliente atualizado = Cliente.fromPersisted(1L, "João Lima", "11999990001",
                null, null, null, null, null, Instant.now());
        when(clienteUseCase.atualizar(eq(1L), any(), any(), any(), any(), any(), any())).thenReturn(atualizado);

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"João Lima","telefone":"11999990001"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Lima"));
    }

    @Test
    void atualizar_retorna_404_quando_nao_existe() throws Exception {
        when(clienteUseCase.atualizar(eq(99L), any(), any(), any(), any(), any(), any()))
                .thenThrow(new ClienteNotFoundException(99L));

        mockMvc.perform(put("/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"X","telefone":"11900000000"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_retorna_204() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteUseCase).remover(1L);
    }

    @Test
    void remover_retorna_404_quando_nao_existe() throws Exception {
        doThrow(new ClienteNotFoundException(99L)).when(clienteUseCase).remover(99L);

        mockMvc.perform(delete("/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_retorna_409_quando_tem_os_em_aberto() throws Exception {
        doThrow(new ClienteTemOSEmAbertoException(1L)).when(clienteUseCase).remover(1L);

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void listarInstrumentos_retorna_200_com_lista() throws Exception {
        Instrumento inst = Instrumento.fromPersisted(1L, TipoInstrumento.GUITARRA, "Fender",
                "Stratocaster", "SN001", "Sunburst", null, 1L, Instant.now());
        when(instrumentoUseCase.listarPorCliente(1L)).thenReturn(List.of(inst));

        mockMvc.perform(get("/clientes/1/instrumentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Fender"));
    }
}
