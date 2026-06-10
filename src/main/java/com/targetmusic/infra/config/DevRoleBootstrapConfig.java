package com.targetmusic.infra.config;

import com.targetmusic.core.ports.in.PermissionUseCase;
import com.targetmusic.core.ports.in.RoleUseCase;
import com.targetmusic.core.ports.in.UserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Garante que ROLE_DEV e roles de negócio (ROLE_ATENDENTE, ROLE_TECNICO, ROLE_CLIENTE) existam
 * em todos os ambientes. As migrations V51/V52 são a fonte autoritativa; este bootstrap é a
 * salvaguarda programática para ambientes onde a migration pode ter rodado antes da feature.
 */
@Configuration
@Profile({"dev", "hml", "prod"})
public class DevRoleBootstrapConfig {

    private static final Logger log = LoggerFactory.getLogger(DevRoleBootstrapConfig.class);

    private static final String[] ADMIN_PERMISSIONS = {
        "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE", "USER_ROLE_ASSIGN", "USER_STATUS",
        "ROLE_READ", "ROLE_MANAGE_PERMISSIONS",
        "PERMISSION_READ",
        "AUDIT_READ"
    };

    private static final String[] DEV_ONLY_PERMISSIONS = {
        "DEV_ROLE_MANAGE",
        "DEV_PERMISSION_MANAGE"
    };

    private static final String[] BUSINESS_PERMISSIONS = {
        "CLIENTE_CREATE", "CLIENTE_READ", "CLIENTE_UPDATE", "CLIENTE_DELETE",
        "INSTRUMENTO_CREATE", "INSTRUMENTO_READ", "INSTRUMENTO_UPDATE", "INSTRUMENTO_DELETE",
        "OS_CREATE", "OS_READ", "OS_UPDATE", "OS_STATUS", "OS_DELETE",
        "OS_ASSIGN_TECNICO", "OS_ORCAMENTO", "OS_ORCAMENTO_APROVAR", "OS_ORCAMENTO_RECUSAR", "OS_ENTREGA"
    };

    private static final String[] ATENDENTE_PERMISSIONS = {
        "CLIENTE_CREATE", "CLIENTE_READ", "CLIENTE_UPDATE",
        "INSTRUMENTO_CREATE", "INSTRUMENTO_READ", "INSTRUMENTO_UPDATE",
        "OS_CREATE", "OS_READ", "OS_UPDATE", "OS_STATUS",
        "OS_ASSIGN_TECNICO", "OS_ORCAMENTO_APROVAR", "OS_ORCAMENTO_RECUSAR", "OS_ENTREGA"
    };

    private static final String[] TECNICO_PERMISSIONS = {
        "CLIENTE_READ",
        "INSTRUMENTO_READ",
        "OS_READ", "OS_UPDATE", "OS_STATUS", "OS_ORCAMENTO"
    };

    private static final String[] CLIENTE_PERMISSIONS = {
        "OS_READ",
        "INSTRUMENTO_READ"
    };

    @Bean
    CommandLineRunner bootstrapDevRole(PermissionUseCase permissionUseCase,
                                       RoleUseCase roleUseCase,
                                       UserUseCase userUseCase,
                                       @Value("${seed.dev.email:}") String devEmail,
                                       @Value("${seed.dev.password:Dev@secure1!}") String devPassword) {
        return args -> {
            ensureDevPermissions(permissionUseCase);
            ensureBusinessPermissions(permissionUseCase);
            ensureDevRole(roleUseCase);
            ensureBusinessRoles(roleUseCase);
            ensureDevUser(userUseCase, devEmail, devPassword);
        };
    }

    private void ensureDevPermissions(PermissionUseCase permissionUseCase) {
        for (String name : ADMIN_PERMISSIONS) {
            tryCreate(() -> permissionUseCase.createPermission(name), "permission", name);
        }
        for (String name : DEV_ONLY_PERMISSIONS) {
            tryCreate(() -> permissionUseCase.createPermission(name), "permission", name);
        }
    }

    private void ensureBusinessPermissions(PermissionUseCase permissionUseCase) {
        for (String name : BUSINESS_PERMISSIONS) {
            tryCreate(() -> permissionUseCase.createPermission(name), "permission", name);
        }
    }

    private void ensureDevRole(RoleUseCase roleUseCase) {
        tryCreate(() -> roleUseCase.createRole("ROLE_DEV"), "role", "ROLE_DEV");

        for (String perm : ADMIN_PERMISSIONS) {
            tryAssign(roleUseCase, "ROLE_DEV", perm);
        }
        for (String perm : DEV_ONLY_PERMISSIONS) {
            tryAssign(roleUseCase, "ROLE_DEV", perm);
        }
        for (String perm : BUSINESS_PERMISSIONS) {
            tryAssign(roleUseCase, "ROLE_DEV", perm);
        }
    }

    private void ensureBusinessRoles(RoleUseCase roleUseCase) {
        ensureRole(roleUseCase, "ROLE_ATENDENTE", ATENDENTE_PERMISSIONS);
        ensureRole(roleUseCase, "ROLE_TECNICO",   TECNICO_PERMISSIONS);
        ensureRole(roleUseCase, "ROLE_CLIENTE",   CLIENTE_PERMISSIONS);

        for (String perm : BUSINESS_PERMISSIONS) {
            tryAssign(roleUseCase, "ROLE_ADMIN", perm);
        }
    }

    private void ensureRole(RoleUseCase roleUseCase, String roleName, String[] permissions) {
        tryCreate(() -> roleUseCase.createRole(roleName), "role", roleName);
        for (String perm : permissions) {
            tryAssign(roleUseCase, roleName, perm);
        }
    }

    private void ensureDevUser(UserUseCase userUseCase, String devEmail, String devPassword) {
        if (devEmail.isBlank()) return;
        String devUsername = devEmail.contains("@") ? devEmail.split("@")[0] : devEmail;
        if (userUseCase.findByUsername(devUsername).isEmpty()) {
            userUseCase.createUser(devUsername, devPassword, List.of("ROLE_DEV"));
            log.info("dev-bootstrap.user.created username={} env={}", devUsername,
                    System.getProperty("spring.profiles.active", "unknown"));
        }
    }

    private void tryCreate(Runnable action, String type, String name) {
        try { action.run(); }
        catch (Exception e) { log.debug("dev-bootstrap.{}.skip name={}", type, name); }
    }

    private void tryAssign(RoleUseCase roleUseCase, String role, String perm) {
        try { roleUseCase.assignPermission(role, perm); }
        catch (Exception e) { log.debug("dev-bootstrap.role.assign.skip role={} perm={}", role, perm); }
    }
}
