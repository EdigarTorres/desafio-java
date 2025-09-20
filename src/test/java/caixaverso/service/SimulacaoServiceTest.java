package caixaverso.service;

import caixaverso.dto.SimulacaoRequest;
import caixaverso.dto.SimulacaoResponse;
import caixaverso.model.ProdutoEmprestimo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SimulacaoServiceTest {

    private EntityManager em;
    private SimulacaoService service;

    @BeforeEach
    void setup() {
        em = Mockito.mock(EntityManager.class);
        service = new SimulacaoService(em);
    }

    @Test
    void deveSimularEmprestimoComDadosValidos() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Teste", new BigDecimal("12.0"), 60);
        produto.setId(1L);

        Mockito.when(em.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        SimulacaoRequest request = new SimulacaoRequest(1L, 10000.0, 12);
        SimulacaoResponse response = service.simular(request);

        assertEquals("10000.00", response.valorSolicitado());
        assertEquals(12, response.prazoMeses());
        assertEquals("12.00", response.taxaJurosAnual());
        assertEquals(12, response.memoriaCalculo().size());
    }

    @Test
    void deveLancarExcecaoSeProdutoNaoExiste() {
        Mockito.when(em.find(ProdutoEmprestimo.class, 99L)).thenReturn(null);
        SimulacaoRequest request = new SimulacaoRequest(99L, 5000.0, 6);

        assertThrows(IllegalArgumentException.class, () -> service.simular(request));
    }
}
