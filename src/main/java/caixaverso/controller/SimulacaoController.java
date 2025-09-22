package caixaverso.controller;

import caixaverso.dto.SimulacaoRequest;
import caixaverso.dto.SimulacaoResponse;
import caixaverso.service.SimulacaoService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @POST
    @Operation(
            summary = "Simula um empréstimo com base em um produto.",
            description = "Recebe o ID do produto, valor solicitado e prazo em meses. Retorna o cálculo detalhado."
    )
    public Response simular(SimulacaoRequest request) {
        SimulacaoResponse response = simulacaoService.simular(request);
        return Response.ok(response).build();
    }
}
