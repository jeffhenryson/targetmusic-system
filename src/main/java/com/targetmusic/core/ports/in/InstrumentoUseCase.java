package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;

import java.util.Collection;
import java.util.Map;

public interface InstrumentoUseCase {
    Instrumento criar(TipoInstrumento tipo, String marca, String modelo, Long clienteId,
                      String numeroDeSerie, String cor, String descricao);
    Instrumento buscarPorId(Long id);
    Map<Long, Instrumento> buscarPorIds(Collection<Long> ids);
    PageResult<Instrumento> listarPorCliente(Long clienteId, int page, int size);
    Instrumento atualizar(Long id, TipoInstrumento tipo, String marca, String modelo,
                          String numeroDeSerie, String cor, String descricao);
    void remover(Long id);
}
