package com.targetmusic.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
@Sql(statements = "CREATE SEQUENCE IF NOT EXISTS os_numero_seq START WITH 1",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class OSFlowIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // ── Fluxo feliz completo ──────────────────────────────────────────────────

    @Test
    void fluxo_completo_criar_os_ate_entrega() throws Exception {
        RequestPostProcessor admin = admin();

        // 1. Criar cliente
        MvcResult clienteResult = mockMvc.perform(post("/clientes")
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Cliente Teste Flow","telefone":"11900010001",
                                 "email":"fluxo@test.com","cpf":"98765432100"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        Long clienteId = om.readTree(clienteResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Criar instrumento
        ObjectNode instrBody = om.createObjectNode();
        instrBody.put("tipo", "GUITARRA");
        instrBody.put("marca", "Fender");
        instrBody.put("modelo", "Stratocaster Flow");
        instrBody.put("clienteId", clienteId);

        MvcResult instrResult = mockMvc.perform(post("/instrumentos")
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(instrBody)))
                .andExpect(status().isCreated())
                .andReturn();
        Long instrumentoId = om.readTree(instrResult.getResponse().getContentAsString()).get("id").asLong();

        // 3. Abrir OS
        ObjectNode osBody = om.createObjectNode();
        osBody.put("clienteId", clienteId);
        osBody.put("instrumentoId", instrumentoId);
        osBody.put("descricaoProblema", "Captação com ruído");

        MvcResult osResult = mockMvc.perform(post("/os")
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(osBody)))
                .andExpect(status().isCreated())
                .andReturn();
        Long osId = om.readTree(osResult.getResponse().getContentAsString()).get("id").asLong();

        // 4. Verificar status RECEBIDO
        mockMvc.perform(get("/os/{id}", osId).with(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEBIDO"));

        // 5. Mudar para EM_ANALISE
        mockMvc.perform(patch("/os/{id}/status", osId)
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"novoStatus\":\"EM_ANALISE\"}"))
                .andExpect(status().isNoContent());

        // 6. Definir orçamento (muda status para AGUARDANDO_APROVACAO)
        mockMvc.perform(patch("/os/{id}/orcamento", osId)
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valor\":350.00,\"prazoEstimado\":\"2026-07-01\"}"))
                .andExpect(status().isNoContent());

        // 7. Verificar status AGUARDANDO_APROVACAO
        mockMvc.perform(get("/os/{id}", osId).with(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGUARDANDO_APROVACAO"));

        // 8. Aprovar orçamento (muda status para EM_MANUTENCAO)
        mockMvc.perform(patch("/os/{id}/orcamento/aprovar", osId)
                        .with(admin))
                .andExpect(status().isNoContent());

        // 9. Mudar para PRONTO
        mockMvc.perform(patch("/os/{id}/status", osId)
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"novoStatus\":\"PRONTO\"}"))
                .andExpect(status().isNoContent());

        // 10. Registrar entrega (muda status para ENTREGUE)
        mockMvc.perform(patch("/os/{id}/entrega", osId)
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valorFinal\":350.00}"))
                .andExpect(status().isNoContent());

        // 11. Verificar status ENTREGUE
        mockMvc.perform(get("/os/{id}", osId).with(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"));
    }

    // ── Fluxos de erro ────────────────────────────────────────────────────────

    @Test
    void abrir_os_com_cliente_inexistente_retorna_404() throws Exception {
        mockMvc.perform(post("/os")
                        .with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":999999,\"instrumentoId\":999999,\"descricaoProblema\":\"Teste\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void transicao_status_invalida_retorna_422() throws Exception {
        RequestPostProcessor admin = admin();

        // Criar cliente e instrumento mínimos para abrir OS
        MvcResult clienteResult = mockMvc.perform(post("/clientes")
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Cliente Erro Status","telefone":"11900020002",
                                 "email":"errostatus@test.com","cpf":"11122233300"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        Long clienteId = om.readTree(clienteResult.getResponse().getContentAsString()).get("id").asLong();

        ObjectNode instrBody = om.createObjectNode();
        instrBody.put("tipo", "BAIXO");
        instrBody.put("marca", "Gibson");
        instrBody.put("modelo", "SG Bass");
        instrBody.put("clienteId", clienteId);

        MvcResult instrResult = mockMvc.perform(post("/instrumentos")
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(instrBody)))
                .andExpect(status().isCreated())
                .andReturn();
        Long instrumentoId = om.readTree(instrResult.getResponse().getContentAsString()).get("id").asLong();

        MvcResult osResult = mockMvc.perform(post("/os")
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":" + clienteId + ",\"instrumentoId\":" + instrumentoId
                                 + ",\"descricaoProblema\":\"Teste transição inválida\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        Long osId = om.readTree(osResult.getResponse().getContentAsString()).get("id").asLong();

        // Tentar ir de RECEBIDO → PRONTO diretamente (inválido)
        mockMvc.perform(patch("/os/{id}/status", osId)
                        .with(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"novoStatus\":\"PRONTO\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    private RequestPostProcessor admin() {
        String[] perms = {
            "ROLE_ADMIN",
            "CLIENTE_CREATE", "CLIENTE_READ", "CLIENTE_UPDATE", "CLIENTE_DELETE",
            "INSTRUMENTO_CREATE", "INSTRUMENTO_READ", "INSTRUMENTO_UPDATE", "INSTRUMENTO_DELETE",
            "OS_CREATE", "OS_READ", "OS_UPDATE", "OS_STATUS", "OS_DELETE",
            "OS_ASSIGN_TECNICO", "OS_ORCAMENTO", "OS_ORCAMENTO_APROVAR", "OS_ORCAMENTO_RECUSAR", "OS_ENTREGA"
        };
        GrantedAuthority[] authorities = Arrays.stream(perms)
                .map(SimpleGrantedAuthority::new)
                .toArray(GrantedAuthority[]::new);
        return user("admin").authorities(authorities);
    }
}
