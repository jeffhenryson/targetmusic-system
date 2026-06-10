package com.targetmusic.core.ports.out.audit;

import com.targetmusic.core.domain.event.AuditEvent;
import com.targetmusic.core.domain.model.AuditLogEntry;
import com.targetmusic.core.domain.model.PageResult;

import java.time.Instant;
import java.util.Set;

public interface AuditLogRepository {
    void save(AuditEvent event, String ipAddress);
    PageResult<AuditLogEntry> findFiltered(String username, String action, Instant from, Instant to, int page, int size, Set<String> excludeActions);
    /** Remove entradas mais antigas que {@code cutoff}. Chamado pelo scheduler de retenção. */
    long deleteOlderThan(Instant cutoff);
}
