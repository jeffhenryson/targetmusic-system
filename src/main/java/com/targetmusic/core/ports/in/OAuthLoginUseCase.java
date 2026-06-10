package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.auth.OAuthLoginResult;

public interface OAuthLoginUseCase {
    OAuthLoginResult loginWithGoogle(String idToken);
}
