package com.targetmusic.infra.config;

import com.targetmusic.core.domain.model.rbac.Role;
import com.targetmusic.core.ports.in.RoleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties =
    "spring.datasource.url=jdbc:h2:mem:rbac_bootstrap_it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1")
class DevRoleBootstrapIT {

    @Autowired
    private RoleUseCase roleUseCase;

    @Test
    void role_atendente_existe_com_permissoes_corretas() {
        Role role = roleUseCase.findByName("ROLE_ATENDENTE");
        Set<String> perms = permNames(role);

        assertThat(perms).containsAll(Set.of(
            "CLIENTE_CREATE", "CLIENTE_READ", "CLIENTE_UPDATE",
            "INSTRUMENTO_CREATE", "INSTRUMENTO_READ", "INSTRUMENTO_UPDATE",
            "OS_CREATE", "OS_READ", "OS_UPDATE", "OS_STATUS",
            "OS_ASSIGN_TECNICO", "OS_ORCAMENTO_APROVAR", "OS_ORCAMENTO_RECUSAR", "OS_ENTREGA"
        ));
        assertThat(perms).doesNotContain(
            "CLIENTE_DELETE", "INSTRUMENTO_DELETE", "OS_DELETE", "OS_ORCAMENTO"
        );
    }

    @Test
    void role_tecnico_existe_com_permissoes_corretas() {
        Role role = roleUseCase.findByName("ROLE_TECNICO");
        Set<String> perms = permNames(role);

        assertThat(perms).containsAll(Set.of(
            "CLIENTE_READ", "INSTRUMENTO_READ",
            "OS_READ", "OS_UPDATE", "OS_STATUS", "OS_ORCAMENTO"
        ));
        assertThat(perms).doesNotContain(
            "CLIENTE_CREATE", "CLIENTE_DELETE",
            "OS_CREATE", "OS_DELETE", "OS_ASSIGN_TECNICO",
            "OS_ORCAMENTO_APROVAR", "OS_ORCAMENTO_RECUSAR", "OS_ENTREGA"
        );
    }

    @Test
    void role_cliente_existe_com_permissoes_corretas() {
        Role role = roleUseCase.findByName("ROLE_CLIENTE");
        Set<String> perms = permNames(role);

        assertThat(perms).containsAll(Set.of("OS_READ", "INSTRUMENTO_READ"));
        assertThat(perms).doesNotContain(
            "OS_CREATE", "OS_STATUS", "OS_DELETE",
            "CLIENTE_CREATE", "CLIENTE_READ",
            "OS_ASSIGN_TECNICO", "OS_ORCAMENTO"
        );
    }

    @Test
    void role_admin_tem_todas_permissoes_de_negocio() {
        Role role = roleUseCase.findByName("ROLE_ADMIN");
        Set<String> perms = permNames(role);

        assertThat(perms).containsAll(Set.of(
            "CLIENTE_CREATE", "CLIENTE_READ", "CLIENTE_UPDATE", "CLIENTE_DELETE",
            "INSTRUMENTO_CREATE", "INSTRUMENTO_READ", "INSTRUMENTO_UPDATE", "INSTRUMENTO_DELETE",
            "OS_CREATE", "OS_READ", "OS_UPDATE", "OS_STATUS", "OS_DELETE",
            "OS_ASSIGN_TECNICO", "OS_ORCAMENTO", "OS_ORCAMENTO_APROVAR", "OS_ORCAMENTO_RECUSAR", "OS_ENTREGA"
        ));
    }

    private Set<String> permNames(Role role) {
        return role.getPermissions().stream()
            .map(p -> p.getName())
            .collect(Collectors.toSet());
    }
}
