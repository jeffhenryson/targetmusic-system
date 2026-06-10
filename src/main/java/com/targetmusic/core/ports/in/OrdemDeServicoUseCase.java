package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.os.HistoricoOS;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.domain.model.os.StatusOS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrdemDeServicoUseCase {
    // Sprint C
    OrdemDeServico abrir(Long instrumentoId, Long clienteId, String atendenteUsername,
                         String descricaoProblema, String observacoes);
    OrdemDeServico buscarPorId(Long id);
    OrdemDeServico buscarPorNumero(String numero);
    PageResult<OrdemDeServico> listar(StatusOS status, Long clienteId, String tecnicoUsername, int page, int size);
    List<OrdemDeServico> listarPorCliente(Long clienteId);
    void adicionarTecnico(Long osId, String tecnicoUsername);
    void removerTecnico(Long osId, String tecnicoUsername);
    void atualizarStatus(Long osId, StatusOS novoStatus, String usuarioUsername, String observacao);
    List<HistoricoOS> buscarHistorico(Long osId);

    // Sprint D
    void definirOrcamento(Long osId, BigDecimal valor, LocalDate prazoEstimado, String usuarioUsername);
    void aprovarOrcamento(Long osId, String usuarioUsername);
    void recusarOrcamento(Long osId, String observacao, String usuarioUsername);
    void registrarEntrega(Long osId, BigDecimal valorFinal, String usuarioUsername);
    OrdemDeServico atualizar(Long osId, String laudoTecnico, LocalDate prazoEstimado, String observacoes);
    void remover(Long osId);
}
