package com.targetmusic.core.ports.out.twofa;

import com.targetmusic.core.domain.model.auth.TotpChallengeToken;

import java.time.Instant;
import java.util.Optional;

public interface TotpChallengeTokenRepository {
    TotpChallengeToken save(String username, String rawToken, Instant expiresAt);

    Optional<TotpChallengeToken> findByToken(String rawToken);

    boolean markAsUsed(String rawToken);

    void deleteByUsername(String username);

    void deleteExpiredBefore(Instant before);
}
