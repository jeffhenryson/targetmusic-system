package com.targetmusic.core.domain.exception.auth;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException() {
        super("Refresh token has expired");
    }
}
