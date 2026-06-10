package com.targetmusic.core.domain.model;

public record StatsResult(
    long totalUsers,
    long activeUsers,
    long disabledUsers,
    long totalRoles,
    long totalPermissions
) {}
