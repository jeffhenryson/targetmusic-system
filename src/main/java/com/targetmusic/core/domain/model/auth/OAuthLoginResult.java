package com.targetmusic.core.domain.model.auth;

public record OAuthLoginResult(TokenPair tokenPair, String username) {}
