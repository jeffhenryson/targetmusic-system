package com.targetmusic.core.ports.in;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.os.HistoricoOS;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.domain.model.os.StatusOS;

import java.util.List;

public interface OrdemDeServicoUseCase {
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
}
