package com.targetmusic.core.ports.out.instrumento;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.instrumento.Instrumento;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InstrumentoRepository {
    Instrumento save(Instrumento instrumento);
    Optional<Instrumento> findById(Long id);
    PageResult<Instrumento> findByClienteId(Long clienteId, int page, int size);
    List<Instrumento> findAllByIdIn(Collection<Long> ids);
    void deleteById(Long id);
    boolean hasOpenOS(Long instrumentoId);
}
