package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.rbac.Permission;

public interface PermissionUseCase {
    Permission createPermission(String name);
    PageResult<Permission> listAll(int page, int size);
    Permission findByName(String name);
    void deletePermission(String name);
}
