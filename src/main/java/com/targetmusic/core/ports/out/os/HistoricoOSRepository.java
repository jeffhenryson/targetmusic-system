package com.targetmusic.core.ports.out.os;

import com.targetmusic.core.domain.model.os.HistoricoOS;

import java.util.List;

public interface HistoricoOSRepository {
    HistoricoOS save(HistoricoOS historico);
    List<HistoricoOS> findByOsId(Long osId);
}
