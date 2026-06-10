package com.targetmusic.core.service;

import com.targetmusic.core.domain.exception.cliente.ClienteNotFoundException;
import com.targetmusic.core.domain.exception.cliente.ClienteTemOSEmAbertoException;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.ports.in.ClienteUseCase;
import com.targetmusic.core.ports.out.cliente.ClienteRepository;
import org.springframework.transaction.annotation.Transactional;

public class ClienteService implements ClienteUseCase {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public Cliente criar(String nome, String telefone, String email, String cpf,
                         String endereco, String observacoes) {
        Cliente cliente = Cliente.of(nome, telefone);
        cliente.atualizar(nome, telefone, email, cpf, endereco, observacoes);
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Cliente> listar(String search, int page, int size) {
        return clienteRepository.findAll(search, page, size);
    }

    @Override
    @Transactional
    public Cliente atualizar(Long id, String nome, String telefone, String email,
                             String cpf, String endereco, String observacoes) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        cliente.atualizar(nome, telefone, email, cpf, endereco, observacoes);
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        if (clienteRepository.hasOpenOS(cliente.getId())) {
            throw new ClienteTemOSEmAbertoException(id);
        }
        clienteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void vincularUsuario(Long clienteId, Long userId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException(clienteId));
        cliente.vincularUsuario(userId);
        clienteRepository.save(cliente);
    }
}
