package caixaverso.validator;

import caixaverso.dao.ProdutoDao;
import caixaverso.dto.SimulacaoRequest;
import caixaverso.model.ProdutoEmprestimo;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@QuarkusTest
class SimulacaoValidatorTest {

    @Inject
    SimulacaoValidator simulacaoValidator;

    @InjectMock
    ProdutoDao produtoDao;

    private ProdutoEmprestimo produto;

    @BeforeEach
    void setUp() {
        produto = new ProdutoEmprestimo();
        produto.setId(1L);
        produto.setNome("Crédito Pessoal");
        produto.setTaxaJurosAnual(new BigDecimal("19.9"));
        produto.setPrazoMaximoMeses(24);
    }

    @Test
    @DisplayName("Deve validar com sucesso e retornar o produto quando a requisição é válida")
    void deveValidarComSucesso_quandoRequestEhValida() {

        SimulacaoRequest request = new SimulacaoRequest(1L, 10000.0, 12);
        Mockito.when(produtoDao.listarPorId(1L)).thenReturn(produto);

        ProdutoEmprestimo result = assertDoesNotThrow(() -> simulacaoValidator.validateAndGetProduto(request));

        assertNotNull(result);
        assertEquals(produto.getId(), result.getId());
        Mockito.verify(produtoDao).listarPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o produto não é encontrado")
    void deveLancarExcecao_quandoProdutoNaoEncontrado() {

        SimulacaoRequest request = new SimulacaoRequest(99L, 10000.0, 12);
        Mockito.when(produtoDao.listarPorId(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> simulacaoValidator.validateAndGetProduto(request));

        assertEquals("Produto de empréstimo não encontrado para o ID informado.", exception.getMessage());
        Mockito.verify(produtoDao).listarPorId(99L);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o prazo solicitado excede o máximo do produto")
    void deveLancarExcecao_quandoPrazoExcedeMaximo() {

        int prazoExcedido = 36;
        SimulacaoRequest request = new SimulacaoRequest(1L, 10000.0, prazoExcedido);
        Mockito.when(produtoDao.listarPorId(1L)).thenReturn(produto);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> simulacaoValidator.validateAndGetProduto(request));

        String expectedMessage = String.format(
                "O prazo solicitado (%d meses) excede o prazo máximo do produto (%d meses).",
                prazoExcedido, produto.getPrazoMaximoMeses()
        );
        assertEquals(expectedMessage, exception.getMessage());
        Mockito.verify(produtoDao).listarPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o ID do produto é nulo")
    void deveLancarExcecao_quandoIdProdutoEhNulo() {

        SimulacaoRequest request = new SimulacaoRequest(null, 10000.0, 12);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> simulacaoValidator.validateAndGetProduto(request));

        assertEquals("O ID do produto é obrigatório.", exception.getMessage());
        Mockito.verify(produtoDao, Mockito.never()).listarPorId(anyLong());
    }

    @ParameterizedTest(name = "Deve lançar exceção para valor solicitado inválido: {0}")
    @ValueSource(doubles = {0.0, -100.0})
    @DisplayName("Deve lançar IllegalArgumentException quando o valor solicitado não é positivo")
    void deveLancarExcecao_quandoValorSolicitadoNaoEhPositivo(Double valorInvalido) {

        SimulacaoRequest request = new SimulacaoRequest(1L, valorInvalido, 12);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> simulacaoValidator.validateAndGetProduto(request));

        assertEquals("O valor solicitado deve ser maior que zero.", exception.getMessage());
        Mockito.verify(produtoDao, Mockito.never()).listarPorId(anyLong());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o valor solicitado excede 1.000.000")
    void deveLancarExcecao_quandoValorSolicitadoExcedeLimite() {

        SimulacaoRequest request = new SimulacaoRequest(1L, 1000000.01, 12);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> simulacaoValidator.validateAndGetProduto(request));

        assertEquals("O valor solicitado deve ser menor ou igual a R$1.000.000,00.", exception.getMessage());
        Mockito.verify(produtoDao, Mockito.never()).listarPorId(anyLong());
    }

    @ParameterizedTest(name = "Deve lançar exceção para prazo inválido: {0}")
    @ValueSource(ints = {0, -1})
    @DisplayName("Deve lançar IllegalArgumentException quando o prazo em meses não é positivo")
    void deveLancarExcecao_quandoPrazoMesesNaoEhPositivo(Integer prazoInvalido) {

        SimulacaoRequest request = new SimulacaoRequest(1L, 10000.0, prazoInvalido);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> simulacaoValidator.validateAndGetProduto(request));

        assertEquals("O prazo em meses deve ser maior que zero.", exception.getMessage());
        Mockito.verify(produtoDao, Mockito.never()).listarPorId(anyLong());
    }
}