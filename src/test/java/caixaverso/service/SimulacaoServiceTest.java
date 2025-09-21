package caixaverso.service;

import caixaverso.dto.SimulacaoRequest;
import caixaverso.dto.SimulacaoResponse;
import caixaverso.model.ProdutoEmprestimo;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class SimulacaoServiceTest {

    @Inject
    SimulacaoService simulacaoService;

    @InjectMock
    EntityManager entityManager;

    private ProdutoEmprestimo produto;

    @BeforeEach
    void setUp() {
        produto = new ProdutoEmprestimo();
        produto.setId(1L);
        produto.setNome("Crédito Pessoal Teste");
        produto.setTaxaJurosAnual(new BigDecimal("12.68"));
        produto.setPrazoMaximoMeses(36);
    }

    @Test
    @DisplayName("Deve simular com sucesso e retornar os cálculos corretos")
    void deveSimularComSucesso_eRetornarCalculosCorretos() {

        SimulacaoRequest request = new SimulacaoRequest(1L, 10000.00, 24);
        when(entityManager.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        SimulacaoResponse response = simulacaoService.simular(request);

        assertNotNull(response);
        assertEquals(produto.getId(), response.produto().getId());
        assertEquals("10000.00", response.valorSolicitado());
        assertEquals(24, response.prazoMeses());
        assertEquals("470.72", response.valorParcelaMensal());
        assertEquals("11297.28", response.valorTotalComJuros());
        assertEquals(24, response.memoriaCalculo().size());
        assertEquals("0.11", response.memoriaCalculo().get(23).saldoDevedor());

        Mockito.verify(entityManager).find(ProdutoEmprestimo.class, 1L);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o produto não for encontrado")
    void deveLancarIllegalArgumentException_quandoProdutoNaoEncontrado() {

        long idInexistente = 99L;
        SimulacaoRequest requestComIdInexistente = new SimulacaoRequest(idInexistente, 10000.0, 12);
        when(entityManager.find(ProdutoEmprestimo.class, idInexistente)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulacaoService.simular(requestComIdInexistente));

        assertEquals("Produto de empréstimo não encontrado para o ID informado.", exception.getMessage());
        Mockito.verify(entityManager).find(ProdutoEmprestimo.class, idInexistente);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o prazo solicitado excede o máximo do produto")
    void deveLancarIllegalArgumentException_quandoPrazoExcedeMaximoDoProduto() {

        int prazoExcedido = 40;
        SimulacaoRequest requestPrazoExcedido = new SimulacaoRequest(1L, 10000.0, prazoExcedido);
        when(entityManager.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulacaoService.simular(requestPrazoExcedido));

        String expectedMessage = String.format(
                "O prazo solicitado (%d meses) excede o prazo máximo do produto (%d meses).",
                prazoExcedido, produto.getPrazoMaximoMeses()
        );
        assertEquals(expectedMessage, exception.getMessage());
    }
}