package caixaverso.controller;

import caixaverso.dto.SimulacaoRequest;
import caixaverso.dto.SimulacaoResponse;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.service.SimulacaoService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class SimulacaoControllerTest {

    @Inject
    SimulacaoController controller;

    @InjectMock
    SimulacaoService simulacaoService;

    private SimulacaoRequest simulacaoRequest;
    private SimulacaoResponse simulacaoResponse;

    @BeforeEach
    void setup() {
        simulacaoRequest = new SimulacaoRequest(1L, 10000.0, 12);

        ProdutoEmprestimo mockProduto = new ProdutoEmprestimo("Produto Teste", new BigDecimal("10.00"), 12);
        mockProduto.setId(1L);

        simulacaoResponse = new SimulacaoResponse(
                mockProduto,
                "10000.00",
                12,
                "10.00",
                "0.800000",
                "10500.00",
                "875.00",
                Collections.emptyList()
        );
    }

    @Test
    void deveSimularComSucesso() {

        Mockito.when(simulacaoService.simular(any(SimulacaoRequest.class)))
                .thenReturn(simulacaoResponse);

        Response response = controller.simular(simulacaoRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertInstanceOf(SimulacaoResponse.class, response.getEntity());
        SimulacaoResponse actualResponse = (SimulacaoResponse) response.getEntity();
        assertEquals(simulacaoResponse.produto().getId(), actualResponse.produto().getId());
        assertEquals(simulacaoResponse.valorParcelaMensal(), actualResponse.valorParcelaMensal());

        Mockito.verify(simulacaoService).simular(simulacaoRequest);
    }

    @Test
    void deveLancarIllegalArgumentException_quandoServicoLanca() {

        String errorMessage = "Produto de empréstimo não encontrado para o ID informado.";
        Mockito.when(simulacaoService.simular(any(SimulacaoRequest.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.simular(simulacaoRequest);
        });

        assertEquals(errorMessage, exception.getMessage());
        Mockito.verify(simulacaoService).simular(simulacaoRequest);
    }
}
