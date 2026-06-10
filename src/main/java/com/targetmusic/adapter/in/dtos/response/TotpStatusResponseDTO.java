package com.targetmusic.adapter.in.dtos.response;

public record TotpStatusResponseDTO(boolean enabled, int backupCodesRemaining) {}
