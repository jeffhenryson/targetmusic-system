package com.targetmusic.adapter.in.dtos.response;

public record StatsResponseDTO(
    long totalUsers,
    long activeUsers,
    long disabledUsers,
    long totalRoles,
    long totalPermissions
) {}
