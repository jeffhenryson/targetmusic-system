package com.targetmusic.core.ports.out.os;

import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.domain.model.os.StatusOS;

import java.util.List;
import java.util.Optional;

public interface OrdemDeServicoRepository {
    OrdemDeServico save(OrdemDeServico os);
    Optional<OrdemDeServico> findById(Long id);
    Optional<OrdemDeServico> findByNumero(String numero);
    PageResult<OrdemDeServico> findAll(StatusOS status, Long clienteId, String tecnicoUsername, int page, int size);
    List<OrdemDeServico> findByClienteId(Long clienteId);
    void deleteById(Long id);
}
