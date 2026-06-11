package com.targetmusic.adapter.in.controller;

import com.targetmusic.adapter.in.converter.ClienteDTOConverter;
import com.targetmusic.adapter.in.converter.InstrumentoDTOConverter;
import com.targetmusic.adapter.in.converter.OrdemDeServicoDTOConverter;
import com.targetmusic.adapter.in.dtos.request.ClienteRequest;
import com.targetmusic.core.domain.exception.cliente.ClienteNaoVinculadoException;
import com.targetmusic.adapter.in.dtos.response.ClienteResponse;
import com.targetmusic.adapter.in.dtos.response.InstrumentoResponse;
import com.targetmusic.adapter.in.dtos.response.OSResponse;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.ports.in.ClienteUseCase;
import com.targetmusic.core.ports.in.InstrumentoUseCase;
import com.targetmusic.core.ports.in.OrdemDeServicoUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteUseCase clienteUseCase;
    private final InstrumentoUseCase instrumentoUseCase;
    private final OrdemDeServicoUseCase osUseCase;
    private final ClienteDTOConverter clienteConverter;
    private final InstrumentoDTOConverter instrumentoConverter;
    private final OrdemDeServicoDTOConverter osConverter;

    public ClienteController(ClienteUseCase clienteUseCase, InstrumentoUseCase instrumentoUseCase,
                              OrdemDeServicoUseCase osUseCase,
                              ClienteDTOConverter clienteConverter,
                              InstrumentoDTOConverter instrumentoConverter,
                              OrdemDeServicoDTOConverter osConverter) {
        this.clienteUseCase = clienteUseCase;
        this.instrumentoUseCase = instrumentoUseCase;
        this.osUseCase = osUseCase;
        this.clienteConverter = clienteConverter;
        this.instrumentoConverter = instrumentoConverter;
        this.osConverter = osConverter;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE_CREATE')")
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteUseCase.criar(request.nome(), request.telefone(),
                request.email(), request.cpf(), request.endereco(), request.observacoes());
        return ResponseEntity.created(URI.create("/clientes/" + cliente.getId()))
                .body(clienteConverter.toResponse(cliente));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTE_READ')")
    public ResponseEntity<PageResult<ClienteResponse>> listar(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int capped = Math.min(size, 100);
        PageResult<Cliente> result = clienteUseCase.listar(search, page, capped);
        PageResult<ClienteResponse> response = new PageResult<>(
                result.content().stream().map(clienteConverter::toResponse).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_READ')")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteConverter.toResponse(clienteUseCase.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_UPDATE')")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteUseCase.atualizar(id, request.nome(), request.telefone(),
                request.email(), request.cpf(), request.endereco(), request.observacoes());
        return ResponseEntity.ok(clienteConverter.toResponse(cliente));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_DELETE')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        clienteUseCase.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/instrumentos")
    @PreAuthorize("hasAuthority('INSTRUMENTO_READ')")
    public ResponseEntity<PageResult<InstrumentoResponse>> listarInstrumentos(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        if (isCliente(authentication)) {
            long meuClienteId = resolverClienteId(authentication);
            if (meuClienteId != id) {
                throw new org.springframework.security.access.AccessDeniedException("Acesso negado");
            }
        }
        int capped = Math.min(size, 100);
        PageResult<Instrumento> result = instrumentoUseCase.listarPorCliente(id, page, capped);
        return ResponseEntity.ok(new PageResult<>(
                result.content().stream().map(instrumentoConverter::toResponse).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages()));
    }

    @GetMapping("/{id}/os")
    @PreAuthorize("hasAuthority('OS_READ')")
    public ResponseEntity<PageResult<OSResponse>> listarOS(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        if (isCliente(authentication)) {
            long meuClienteId = resolverClienteId(authentication);
            if (meuClienteId != id) {
                throw new org.springframework.security.access.AccessDeniedException("Acesso negado");
            }
        }
        int capped = Math.min(size, 100);
        Cliente cliente = clienteUseCase.buscarPorId(id);
        PageResult<OrdemDeServico> osPage = osUseCase.listarPorCliente(id, page, capped);
        Set<Long> instrumentoIds = osPage.content().stream().map(OrdemDeServico::getInstrumentoId).collect(Collectors.toSet());
        Map<Long, Instrumento> instrumentosMap = instrumentoUseCase.buscarPorIds(instrumentoIds);
        return ResponseEntity.ok(new PageResult<>(
                osPage.content().stream()
                        .map(os -> osConverter.toResponse(os, instrumentosMap.get(os.getInstrumentoId()), cliente))
                        .toList(),
                osPage.page(), osPage.size(), osPage.totalElements(), osPage.totalPages()));
    }

    private boolean isCliente(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_CLIENTE".equals(a.getAuthority()));
    }

    private long resolverClienteId(Authentication authentication) {
        return clienteUseCase.buscarClienteDoUsuario(authentication.getName())
                .orElseThrow(() -> new ClienteNaoVinculadoException(authentication.getName()))
                .getId();
    }
}
