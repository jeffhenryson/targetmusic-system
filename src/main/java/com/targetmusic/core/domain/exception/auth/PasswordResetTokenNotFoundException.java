package com.targetmusic.core.domain.exception.auth;

public class PasswordResetTokenNotFoundException extends RuntimeException {
    public PasswordResetTokenNotFoundException() {
        super("Token de recuperação de senha inválido");
    }
}
