package com.targetmusic.core.service;

import com.targetmusic.core.domain.exception.cliente.ClienteNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoNotFoundException;
import com.targetmusic.core.domain.exception.os.OSNaoPodeSerRemovidaException;
import com.targetmusic.core.domain.exception.os.OrdemDeServicoNotFoundException;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.os.HistoricoOS;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.domain.model.os.StatusOS;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.targetmusic.core.ports.in.OrdemDeServicoUseCase;
import com.targetmusic.core.ports.out.cliente.ClienteRepository;
import com.targetmusic.core.ports.out.instrumento.InstrumentoRepository;
import com.targetmusic.core.ports.out.os.HistoricoOSRepository;
import com.targetmusic.core.ports.out.os.OSNumeroSequencePort;
import com.targetmusic.core.ports.out.os.OrdemDeServicoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public class OrdemDeServicoService implements OrdemDeServicoUseCase {

    private final OrdemDeServicoRepository osRepository;
    private final HistoricoOSRepository historicoRepository;
    private final OSNumeroSequencePort sequencePort;
    private final ClienteRepository clienteRepository;
    private final InstrumentoRepository instrumentoRepository;

    public OrdemDeServicoService(OrdemDeServicoRepository osRepository,
                                  HistoricoOSRepository historicoRepository,
                                  OSNumeroSequencePort sequencePort,
                                  ClienteRepository clienteRepository,
                                  InstrumentoRepository instrumentoRepository) {
        this.osRepository = osRepository;
        this.historicoRepository = historicoRepository;
        this.sequencePort = sequencePort;
        this.clienteRepository = clienteRepository;
        this.instrumentoRepository = instrumentoRepository;
    }

    @Override
    @Transactional
    public OrdemDeServico abrir(Long instrumentoId, Long clienteId, String atendenteUsername,
                                String descricaoProblema, String observacoes) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteId));
        instrumentoRepository.findById(instrumentoId)
                .orElseThrow(() -> new InstrumentoNotFoundException(instrumentoId));

        String numero = sequencePort.nextNumero();
        OrdemDeServico os = OrdemDeServico.abrir(numero, instrumentoId, clienteId,
                atendenteUsername, descricaoProblema);
        if (observacoes != null) {
            os.definirObservacoes(observacoes);
        }
        OrdemDeServico salva = osRepository.save(os);

        historicoRepository.save(new HistoricoOS(null, salva.getId(),
                null, StatusOS.RECEBIDO, atendenteUsername, null, Instant.now()));
        return salva;
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemDeServico buscarPorId(Long id) {
        return osRepository.findById(id)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemDeServico buscarPorNumero(String numero) {
        return osRepository.findByNumero(numero)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(numero));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<OrdemDeServico> listar(StatusOS status, Long clienteId,
                                              String tecnicoUsername, int page, int size) {
        return osRepository.findAll(status, clienteId, tecnicoUsername, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemDeServico> listarPorCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteId));
        return osRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional
    public void adicionarTecnico(Long osId, String tecnicoUsername) {
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        os.adicionarTecnico(tecnicoUsername);
        osRepository.save(os);
    }

    @Override
    @Transactional
    public void removerTecnico(Long osId, String tecnicoUsername) {
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        os.removerTecnico(tecnicoUsername);
        osRepository.save(os);
    }

    @Override
    @Transactional
    public void atualizarStatus(Long osId, StatusOS novoStatus,
                                 String usuarioUsername, String observacao) {
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        StatusOS statusAnterior = os.getStatus();
        os.mudarStatus(novoStatus); // lança TransicaoStatusInvalidaException se inválida
        osRepository.save(os);
        historicoRepository.save(new HistoricoOS(null, osId,
                statusAnterior, novoStatus, usuarioUsername, observacao, Instant.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricoOS> buscarHistorico(Long osId) {
        osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        return historicoRepository.findByOsId(osId);
    }

    @Override
    @Transactional
    public void definirOrcamento(Long osId, BigDecimal valor, LocalDate prazoEstimado,
                                  String usuarioUsername) {
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        StatusOS statusAnterior = os.getStatus();
        os.definirOrcamento(valor);
        if (prazoEstimado != null) os.definirPrazo(prazoEstimado);
        os.mudarStatus(StatusOS.AGUARDANDO_APROVACAO);
        osRepository.save(os);
        historicoRepository.save(new HistoricoOS(null, osId,
                statusAnterior, StatusOS.AGUARDANDO_APROVACAO, usuarioUsername, null, Instant.now()));
    }

    @Override
    @Transactional
    public void aprovarOrcamento(Long osId, String usuarioUsername) {
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        StatusOS statusAnterior = os.getStatus();
        os.mudarStatus(StatusOS.EM_MANUTENCAO);
        osRepository.save(os);
        historicoRepository.save(new HistoricoOS(null, osId,
                statusAnterior, StatusOS.EM_MANUTENCAO, usuarioUsername, "Orçamento aprovado", Instant.now()));
    }

    @Override
    @Transactional
    public void recusarOrcamento(Long osId, String observacao, String usuarioUsername) {
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        StatusOS statusAnterior = os.getStatus();
        os.mudarStatus(StatusOS.CANCELADO);
        os.definirObservacoes(observacao != null ? observacao : "Orçamento recusado");
        osRepository.save(os);
        historicoRepository.save(new HistoricoOS(null, osId,
                statusAnterior, StatusOS.CANCELADO, usuarioUsername, "Orçamento recusado", Instant.now()));
    }

    @Override
    @Transactional
    public void registrarEntrega(Long osId, BigDecimal valorFinal, String usuarioUsername) {
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        StatusOS statusAnterior = os.getStatus();
        if (valorFinal != null) os.definirValorFinal(valorFinal);
        os.registrarEntrega(); // valida PRONTO → ENTREGUE e seta dataEntrega
        osRepository.save(os);
        historicoRepository.save(new HistoricoOS(null, osId,
                statusAnterior, StatusOS.ENTREGUE, usuarioUsername, null, Instant.now()));
    }

    @Override
    @Transactional
    public OrdemDeServico atualizar(Long osId, String laudoTecnico,
                                    LocalDate prazoEstimado, String observacoes) {
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        os.atualizarLaudo(laudoTecnico);
        os.definirPrazo(prazoEstimado);
        os.definirObservacoes(observacoes);
        return osRepository.save(os);
    }

    @Override
    @Transactional
    public void remover(Long osId) {
        OrdemDeServico os = osRepository.findById(osId)
                .orElseThrow(() -> new OrdemDeServicoNotFoundException(osId));
        if (os.getStatus() != StatusOS.CANCELADO && os.getStatus() != StatusOS.RECEBIDO) {
            throw new OSNaoPodeSerRemovidaException(osId, os.getStatus());
        }
        osRepository.deleteById(osId);
    }
}
