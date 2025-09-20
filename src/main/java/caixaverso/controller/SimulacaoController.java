package caixaverso.controller;

import caixaverso.dto.SimulacaoRequest;
import caixaverso.dto.SimulacaoResponse;
import caixaverso.service.SimulacaoService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @POST
    @Operation(
            summary = "Simula um empréstimo com base em um produto",
            description = "Recebe o ID do produto, valor solicitado e prazo em meses. Retorna o cálculo detalhado."
    )
    public Response simular(@Valid SimulacaoRequest request) {
        try {
            SimulacaoResponse response = simulacaoService.simular(request);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErroResponse("Erro de validação", e.getMessage()))
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErroResponse("Erro interno", e.getMessage()))
                    .build();
        }
    }

    public record ErroResponse(String tipo, String mensagem) {
    }
}
