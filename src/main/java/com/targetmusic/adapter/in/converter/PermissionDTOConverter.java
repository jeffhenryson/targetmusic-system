package com.targetmusic.adapter.in.converter;

import com.targetmusic.adapter.in.dtos.response.PermissionResponseDTO;
import com.targetmusic.core.domain.model.rbac.Permission;

public class PermissionDTOConverter {

    public PermissionResponseDTO toResponse(Permission permission) {
        PermissionResponseDTO dto = new PermissionResponseDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        return dto;
    }
}
