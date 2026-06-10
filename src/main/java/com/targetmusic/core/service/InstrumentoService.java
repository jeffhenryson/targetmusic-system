package com.targetmusic.core.service;

import com.targetmusic.core.domain.exception.cliente.ClienteNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoTemOSEmAbertoException;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.instrumento.TipoInstrumento;
import com.targetmusic.core.ports.in.InstrumentoUseCase;
import com.targetmusic.core.ports.out.cliente.ClienteRepository;
import com.targetmusic.core.ports.out.instrumento.InstrumentoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class InstrumentoService implements InstrumentoUseCase {

    private final InstrumentoRepository instrumentoRepository;
    private final ClienteRepository clienteRepository;

    public InstrumentoService(InstrumentoRepository instrumentoRepository,
                               ClienteRepository clienteRepository) {
        this.instrumentoRepository = instrumentoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public Instrumento criar(TipoInstrumento tipo, String marca, String modelo, Long clienteId,
                             String numeroDeSerie, String cor, String descricao) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteId));
        Instrumento instrumento = Instrumento.of(tipo, marca, modelo, clienteId);
        instrumento.atualizar(tipo, marca, modelo, numeroDeSerie, cor, descricao);
        return instrumentoRepository.save(instrumento);
    }

    @Override
    @Transactional(readOnly = true)
    public Instrumento buscarPorId(Long id) {
        return instrumentoRepository.findById(id)
                .orElseThrow(() -> new InstrumentoNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Instrumento> listarPorCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteId));
        return instrumentoRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional
    public Instrumento atualizar(Long id, TipoInstrumento tipo, String marca, String modelo,
                                 String numeroDeSerie, String cor, String descricao) {
        Instrumento instrumento = instrumentoRepository.findById(id)
                .orElseThrow(() -> new InstrumentoNotFoundException(id));
        instrumento.atualizar(tipo, marca, modelo, numeroDeSerie, cor, descricao);
        return instrumentoRepository.save(instrumento);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Instrumento instrumento = instrumentoRepository.findById(id)
                .orElseThrow(() -> new InstrumentoNotFoundException(id));
        if (instrumentoRepository.hasOpenOS(instrumento.getId())) {
            throw new InstrumentoTemOSEmAbertoException(id);
        }
        instrumentoRepository.deleteById(id);
    }
}
