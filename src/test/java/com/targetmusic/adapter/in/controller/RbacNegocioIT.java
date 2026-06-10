package com.targetmusic.adapter.in.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("dev")
class RbacNegocioIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    // ── Sem autenticação → 401 ────────────────────────────────────────────────

    @Test
    void get_clientes_sem_autenticacao_retorna_401() throws Exception {
        mockMvc.perform(get("/clientes"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void post_os_sem_autenticacao_retorna_401() throws Exception {
        mockMvc.perform(post("/os")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void get_instrumentos_sem_autenticacao_retorna_401() throws Exception {
        mockMvc.perform(get("/instrumentos/1"))
            .andExpect(status().isUnauthorized());
    }

    // ── ROLE_ATENDENTE ────────────────────────────────────────────────────────

    @Test
    void atendente_pode_listar_clientes() throws Exception {
        mockMvc.perform(get("/clientes")
            .with(com("CLIENTE_READ")))
            .andExpect(status().isOk());
    }

    @Test
    void atendente_pode_criar_cliente() throws Exception {
        mockMvc.perform(post("/clientes")
            .with(com("CLIENTE_CREATE"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"nome":"João Silva","telefone":"11999990000","email":"joao@test.com","cpf":"12345678901"}
                """))
            .andExpect(status().isCreated());
    }

    @Test
    void atendente_nao_pode_deletar_cliente() throws Exception {
        mockMvc.perform(delete("/clientes/1")
            .with(com("CLIENTE_READ", "CLIENTE_CREATE", "CLIENTE_UPDATE")))
            .andExpect(status().isForbidden());
    }

    @Test
    void atendente_pode_abrir_os_retorna_nao_403() throws Exception {
        // OS_CREATE permitido → chega ao service e falha por regra de negócio (não por segurança)
        mockMvc.perform(post("/os")
            .with(com("OS_CREATE"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"instrumentoId":9999,"clienteId":9999,"descricaoProblema":"Teste RBAC"}
                """))
            .andExpect(status().isNotFound());
    }

    @Test
    void atendente_pode_aprovar_orcamento_retorna_nao_403() throws Exception {
        mockMvc.perform(patch("/os/9999/orcamento/aprovar")
            .with(com("OS_ORCAMENTO_APROVAR")))
            .andExpect(status().isNotFound());
    }

    @Test
    void atendente_pode_recusar_orcamento_retorna_nao_403() throws Exception {
        mockMvc.perform(patch("/os/9999/orcamento/recusar")
            .with(com("OS_ORCAMENTO_RECUSAR")))
            .andExpect(status().isNotFound());
    }

    @Test
    void atendente_nao_pode_definir_orcamento() throws Exception {
        mockMvc.perform(patch("/os/1/orcamento")
            .with(com("OS_CREATE", "OS_READ", "OS_UPDATE"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"valor\":100.00}"))
            .andExpect(status().isForbidden());
    }

    // ── ROLE_TECNICO ──────────────────────────────────────────────────────────

    @Test
    void tecnico_pode_definir_orcamento_retorna_nao_403() throws Exception {
        mockMvc.perform(patch("/os/9999/orcamento")
            .with(com("OS_ORCAMENTO"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"valor\":250.00}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void tecnico_nao_pode_criar_cliente() throws Exception {
        mockMvc.perform(post("/clientes")
            .with(com("CLIENTE_READ", "INSTRUMENTO_READ", "OS_READ"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"X\",\"telefone\":\"11900000000\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void tecnico_nao_pode_aprovar_orcamento() throws Exception {
        mockMvc.perform(patch("/os/1/orcamento/aprovar")
            .with(com("OS_READ", "OS_ORCAMENTO")))
            .andExpect(status().isForbidden());
    }

    @Test
    void tecnico_nao_pode_recusar_orcamento() throws Exception {
        mockMvc.perform(patch("/os/1/orcamento/recusar")
            .with(com("OS_READ", "OS_ORCAMENTO")))
            .andExpect(status().isForbidden());
    }

    @Test
    void tecnico_nao_pode_registrar_entrega() throws Exception {
        mockMvc.perform(patch("/os/1/entrega")
            .with(com("OS_READ", "OS_ORCAMENTO", "OS_STATUS")))
            .andExpect(status().isForbidden());
    }

    @Test
    void tecnico_nao_pode_atribuir_tecnico() throws Exception {
        mockMvc.perform(patch("/os/1/tecnico")
            .with(com("OS_READ", "OS_STATUS"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"tecnicoUsername\":\"t1\",\"acao\":\"ADICIONAR\"}"))
            .andExpect(status().isForbidden());
    }

    // ── ROLE_CLIENTE ──────────────────────────────────────────────────────────

    @Test
    void cliente_pode_ler_os_retorna_nao_403() throws Exception {
        mockMvc.perform(get("/os")
            .with(com("OS_READ")))
            .andExpect(status().isOk());
    }

    @Test
    void cliente_nao_pode_abrir_os() throws Exception {
        mockMvc.perform(post("/os")
            .with(com("OS_READ", "INSTRUMENTO_READ"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"instrumentoId\":1,\"clienteId\":1,\"descricaoProblema\":\"X\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void cliente_nao_pode_criar_instrumento() throws Exception {
        mockMvc.perform(post("/instrumentos")
            .with(com("OS_READ", "INSTRUMENTO_READ"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"tipo\":\"GUITARRA\",\"marca\":\"Fender\",\"modelo\":\"Strat\",\"clienteId\":1}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void cliente_nao_pode_deletar_instrumento() throws Exception {
        mockMvc.perform(delete("/instrumentos/1")
            .with(com("OS_READ", "INSTRUMENTO_READ")))
            .andExpect(status().isForbidden());
    }

    // ── Sem permissão específica → 403 ────────────────────────────────────────

    @Test
    void sem_permissao_adequada_retorna_403_em_clientes() throws Exception {
        mockMvc.perform(get("/clientes")
            .with(com("OS_READ")))
            .andExpect(status().isForbidden());
    }

    @Test
    void sem_permissao_adequada_retorna_403_em_instrumentos() throws Exception {
        mockMvc.perform(get("/instrumentos/1")
            .with(com("OS_READ", "CLIENTE_READ")))
            .andExpect(status().isForbidden());
    }

    private RequestPostProcessor com(String... authorities) {
        GrantedAuthority[] grantedAuthorities = Arrays.stream(authorities)
            .map(SimpleGrantedAuthority::new)
            .toArray(GrantedAuthority[]::new);
        return user("test_user").authorities(grantedAuthorities);
    }
}
