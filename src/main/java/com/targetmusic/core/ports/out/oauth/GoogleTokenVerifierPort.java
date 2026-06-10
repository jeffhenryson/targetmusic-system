package com.targetmusic.core.ports.out.oauth;

import com.targetmusic.core.domain.model.auth.GoogleUserInfo;

public interface GoogleTokenVerifierPort {
    GoogleUserInfo verify(String idToken);
}
