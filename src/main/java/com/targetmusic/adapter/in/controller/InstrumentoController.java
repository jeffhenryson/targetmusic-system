package com.targetmusic.adapter.in.controller;

import com.targetmusic.adapter.in.converter.InstrumentoDTOConverter;
import com.targetmusic.adapter.in.dtos.request.InstrumentoRequest;
import com.targetmusic.adapter.in.dtos.response.InstrumentoResponse;
import com.targetmusic.core.domain.exception.cliente.ClienteNaoVinculadoException;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.ports.in.ClienteUseCase;
import com.targetmusic.core.ports.in.InstrumentoUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/instrumentos")
@SecurityRequirement(name = "bearerAuth")
public class InstrumentoController {

    private final InstrumentoUseCase instrumentoUseCase;
    private final ClienteUseCase clienteUseCase;
    private final InstrumentoDTOConverter converter;

    public InstrumentoController(InstrumentoUseCase instrumentoUseCase,
                                  ClienteUseCase clienteUseCase,
                                  InstrumentoDTOConverter converter) {
        this.instrumentoUseCase = instrumentoUseCase;
        this.clienteUseCase = clienteUseCase;
        this.converter = converter;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INSTRUMENTO_CREATE')")
    public ResponseEntity<InstrumentoResponse> criar(@Valid @RequestBody InstrumentoRequest request) {
        Instrumento instrumento = instrumentoUseCase.criar(request.tipo(), request.marca(),
                request.modelo(), request.clienteId(), request.numeroDeSerie(),
                request.cor(), request.descricao());
        return ResponseEntity.created(URI.create("/instrumentos/" + instrumento.getId()))
                .body(converter.toResponse(instrumento));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTRUMENTO_READ')")
    public ResponseEntity<InstrumentoResponse> buscarPorId(@PathVariable Long id,
                                                            Authentication authentication) {
        Instrumento instrumento = instrumentoUseCase.buscarPorId(id);
        if (isCliente(authentication)) {
            long meuClienteId = resolverClienteId(authentication);
            if (!instrumento.getClienteId().equals(meuClienteId)) {
                throw new org.springframework.security.access.AccessDeniedException("Acesso negado");
            }
        }
        return ResponseEntity.ok(converter.toResponse(instrumento));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTRUMENTO_UPDATE')")
    public ResponseEntity<InstrumentoResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody InstrumentoRequest request) {
        Instrumento instrumento = instrumentoUseCase.atualizar(id, request.tipo(), request.marca(),
                request.modelo(), request.numeroDeSerie(), request.cor(), request.descricao());
        return ResponseEntity.ok(converter.toResponse(instrumento));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTRUMENTO_DELETE')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        instrumentoUseCase.remover(id);
        return ResponseEntity.noContent().build();
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
