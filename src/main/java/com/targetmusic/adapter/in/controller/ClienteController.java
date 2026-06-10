package com.targetmusic.adapter.in.controller;

import com.targetmusic.adapter.in.converter.ClienteDTOConverter;
import com.targetmusic.adapter.in.converter.InstrumentoDTOConverter;
import com.targetmusic.adapter.in.dtos.request.ClienteRequest;
import com.targetmusic.adapter.in.dtos.response.ClienteResponse;
import com.targetmusic.adapter.in.dtos.response.InstrumentoResponse;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.ports.in.ClienteUseCase;
import com.targetmusic.core.ports.in.InstrumentoUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteUseCase clienteUseCase;
    private final InstrumentoUseCase instrumentoUseCase;
    private final ClienteDTOConverter clienteConverter;
    private final InstrumentoDTOConverter instrumentoConverter;

    public ClienteController(ClienteUseCase clienteUseCase, InstrumentoUseCase instrumentoUseCase,
                              ClienteDTOConverter clienteConverter, InstrumentoDTOConverter instrumentoConverter) {
        this.clienteUseCase = clienteUseCase;
        this.instrumentoUseCase = instrumentoUseCase;
        this.clienteConverter = clienteConverter;
        this.instrumentoConverter = instrumentoConverter;
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
    public ResponseEntity<List<InstrumentoResponse>> listarInstrumentos(@PathVariable Long id) {
        List<Instrumento> instrumentos = instrumentoUseCase.listarPorCliente(id);
        return ResponseEntity.ok(instrumentos.stream().map(instrumentoConverter::toResponse).toList());
    }
}
