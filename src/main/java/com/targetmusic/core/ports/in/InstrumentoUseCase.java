package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;

import java.util.List;

public interface InstrumentoUseCase {
    Instrumento criar(TipoInstrumento tipo, String marca, String modelo, Long clienteId,
                      String numeroDeSerie, String cor, String descricao);
    Instrumento buscarPorId(Long id);
    List<Instrumento> listarPorCliente(Long clienteId);
    Instrumento atualizar(Long id, TipoInstrumento tipo, String marca, String modelo,
                          String numeroDeSerie, String cor, String descricao);
    void remover(Long id);
}
