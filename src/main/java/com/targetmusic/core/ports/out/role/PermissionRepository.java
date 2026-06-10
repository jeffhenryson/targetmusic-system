package com.targetmusic.core.ports.out.role;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.rbac.Permission;

import java.util.Optional;

public interface PermissionRepository {
    Permission save(Permission permission);
    Optional<Permission> findByName(String name);
    PageResult<Permission> findAll(int page, int size);
    void deleteByName(String name);

    long countAll();
}
