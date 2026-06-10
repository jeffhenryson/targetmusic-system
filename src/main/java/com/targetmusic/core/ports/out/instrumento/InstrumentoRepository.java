package com.targetmusic.core.ports.out.instrumento;

import com.targetmusic.core.domain.model.instrumento.Instrumento;

import java.util.List;
import java.util.Optional;

public interface InstrumentoRepository {
    Instrumento save(Instrumento instrumento);
    Optional<Instrumento> findById(Long id);
    List<Instrumento> findByClienteId(Long clienteId);
    void deleteById(Long id);
    boolean hasOpenOS(Long instrumentoId);
}
