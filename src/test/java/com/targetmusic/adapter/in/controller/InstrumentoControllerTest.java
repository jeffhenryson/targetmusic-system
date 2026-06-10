package com.targetmusic.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.targetmusic.adapter.in.converter.InstrumentoDTOConverter;
import com.targetmusic.core.domain.exception.cliente.ClienteNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoTemOSEmAbertoException;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;
import com.targetmusic.core.ports.in.InstrumentoUseCase;
import com.targetmusic.infra.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class InstrumentoControllerTest {

    private MockMvc mockMvc;
    private InstrumentoUseCase instrumentoUseCase;
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setup() {
        instrumentoUseCase = mock(InstrumentoUseCase.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new InstrumentoController(instrumentoUseCase, new InstrumentoDTOConverter()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Instrumento instrumentoStub() {
        return Instrumento.fromPersisted(1L, TipoInstrumento.GUITARRA, "Fender",
                "Stratocaster", "SN001", "Sunburst", null, 1L, Instant.now());
    }

    @Test
    void criar_retorna_201_com_instrumento() throws Exception {
        when(instrumentoUseCase.criar(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(instrumentoStub());

        mockMvc.perform(post("/instrumentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":1,"tipo":"GUITARRA","marca":"Fender","modelo":"Stratocaster"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.marca").value("Fender"));
    }

    @Test
    void criar_sem_campos_obrigatorios_retorna_400() throws Exception {
        mockMvc.perform(post("/instrumentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":1,"tipo":"GUITARRA"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criar_retorna_404_quando_cliente_nao_existe() throws Exception {
        when(instrumentoUseCase.criar(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new ClienteNotFoundException(99L));

        mockMvc.perform(post("/instrumentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":99,"tipo":"GUITARRA","marca":"Fender","modelo":"Stratocaster"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorId_retorna_200_quando_existe() throws Exception {
        when(instrumentoUseCase.buscarPorId(1L)).thenReturn(instrumentoStub());

        mockMvc.perform(get("/instrumentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("GUITARRA"))
                .andExpect(jsonPath("$.marca").value("Fender"));
    }

    @Test
    void buscarPorId_retorna_404_quando_nao_existe() throws Exception {
        when(instrumentoUseCase.buscarPorId(99L)).thenThrow(new InstrumentoNotFoundException(99L));

        mockMvc.perform(get("/instrumentos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void atualizar_retorna_200() throws Exception {
        Instrumento atualizado = Instrumento.fromPersisted(1L, TipoInstrumento.BAIXO, "Gibson",
                "Les Paul", null, "Black", null, 1L, Instant.now());
        when(instrumentoUseCase.atualizar(eq(1L), any(), any(), any(), any(), any(), any()))
                .thenReturn(atualizado);

        mockMvc.perform(put("/instrumentos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":1,"tipo":"BAIXO","marca":"Gibson","modelo":"Les Paul","cor":"Black"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("Gibson"));
    }

    @Test
    void atualizar_retorna_404_quando_nao_existe() throws Exception {
        when(instrumentoUseCase.atualizar(eq(99L), any(), any(), any(), any(), any(), any()))
                .thenThrow(new InstrumentoNotFoundException(99L));

        mockMvc.perform(put("/instrumentos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":1,"tipo":"GUITARRA","marca":"X","modelo":"Y"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_retorna_204() throws Exception {
        mockMvc.perform(delete("/instrumentos/1"))
                .andExpect(status().isNoContent());

        verify(instrumentoUseCase).remover(1L);
    }

    @Test
    void remover_retorna_404_quando_nao_existe() throws Exception {
        doThrow(new InstrumentoNotFoundException(99L)).when(instrumentoUseCase).remover(99L);

        mockMvc.perform(delete("/instrumentos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_retorna_409_quando_tem_os_em_aberto() throws Exception {
        doThrow(new InstrumentoTemOSEmAbertoException(1L)).when(instrumentoUseCase).remover(1L);

        mockMvc.perform(delete("/instrumentos/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }
}
