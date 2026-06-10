package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.rbac.Role;

public interface RoleUseCase {
    Role createRole(String name);
    PageResult<Role> listAll(int page, int size);
    Role findByName(String name);
    void deleteRole(String name);
    void assignPermission(String roleName, String permissionName);
    void removePermission(String roleName, String permissionName);

    PageResult<Role> findByNameContaining(String search, int page, int size);
}
