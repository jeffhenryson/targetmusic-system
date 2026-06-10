package com.targetmusic.adapter.in.controller;

import com.targetmusic.adapter.in.converter.OrdemDeServicoDTOConverter;
import com.targetmusic.adapter.in.dtos.request.AtribuirTecnicoRequest;
import com.targetmusic.adapter.in.dtos.request.AtualizarStatusRequest;
import com.targetmusic.adapter.in.dtos.request.OSRequest;
import com.targetmusic.adapter.in.dtos.response.HistoricoOSResponse;
import com.targetmusic.adapter.in.dtos.response.OSResponse;
import com.targetmusic.core.domain.model.PageResult;
import com.targetmusic.core.domain.model.cliente.Cliente;
import com.targetmusic.core.domain.model.instrumento.Instrumento;
import com.targetmusic.core.domain.model.os.OrdemDeServico;
import com.targetmusic.core.domain.model.os.StatusOS;
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
import java.util.List;

@RestController
@RequestMapping("/os")
@SecurityRequirement(name = "bearerAuth")
public class OrdemDeServicoController {

    private final OrdemDeServicoUseCase osUseCase;
    private final ClienteUseCase clienteUseCase;
    private final InstrumentoUseCase instrumentoUseCase;
    private final OrdemDeServicoDTOConverter converter;

    public OrdemDeServicoController(OrdemDeServicoUseCase osUseCase,
                                     ClienteUseCase clienteUseCase,
                                     InstrumentoUseCase instrumentoUseCase,
                                     OrdemDeServicoDTOConverter converter) {
        this.osUseCase = osUseCase;
        this.clienteUseCase = clienteUseCase;
        this.instrumentoUseCase = instrumentoUseCase;
        this.converter = converter;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OS_CREATE')")
    public ResponseEntity<OSResponse> abrir(@Valid @RequestBody OSRequest request,
                                             Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "system";
        OrdemDeServico os = osUseCase.abrir(request.instrumentoId(), request.clienteId(),
                username, request.descricaoProblema(), request.observacoes());
        return ResponseEntity.created(URI.create("/os/" + os.getId()))
                .body(toResponse(os));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('OS_READ')")
    public ResponseEntity<PageResult<OSResponse>> listar(
            @RequestParam(required = false) StatusOS status,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String tecnicoUsername,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int capped = Math.min(size, 100);
        PageResult<OrdemDeServico> result = osUseCase.listar(status, clienteId, tecnicoUsername, page, capped);
        PageResult<OSResponse> response = new PageResult<>(
                result.content().stream().map(this::toResponse).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OS_READ')")
    public ResponseEntity<OSResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(osUseCase.buscarPorId(id)));
    }

    @GetMapping("/numero/{numero}")
    @PreAuthorize("hasAuthority('OS_READ')")
    public ResponseEntity<OSResponse> buscarPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(toResponse(osUseCase.buscarPorNumero(numero)));
    }

    @PatchMapping("/{id}/tecnico")
    @PreAuthorize("hasAuthority('OS_ASSIGN_TECNICO')")
    public ResponseEntity<Void> gerenciarTecnico(@PathVariable Long id,
                                                  @Valid @RequestBody AtribuirTecnicoRequest request) {
        if (request.acao() == AtribuirTecnicoRequest.AcaoTecnico.ADICIONAR) {
            osUseCase.adicionarTecnico(id, request.tecnicoUsername());
        } else {
            osUseCase.removerTecnico(id, request.tecnicoUsername());
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('OS_STATUS')")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id,
                                                  @Valid @RequestBody AtualizarStatusRequest request,
                                                  Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "system";
        osUseCase.atualizarStatus(id, request.novoStatus(), username, request.observacao());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historico")
    @PreAuthorize("hasAuthority('OS_READ')")
    public ResponseEntity<List<HistoricoOSResponse>> buscarHistorico(@PathVariable Long id) {
        return ResponseEntity.ok(
                osUseCase.buscarHistorico(id).stream()
                        .map(converter::toHistoricoResponse)
                        .toList()
        );
    }

    private OSResponse toResponse(OrdemDeServico os) {
        Instrumento instrumento = instrumentoUseCase.buscarPorId(os.getInstrumentoId());
        Cliente cliente = clienteUseCase.buscarPorId(os.getClienteId());
        return converter.toResponse(os, instrumento, cliente);
    }
}
