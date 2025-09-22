package caixaverso.validator;

import caixaverso.dto.ProdutoRequest;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoValidatorTest {

    private ProdutoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProdutoValidator();
    }

    @Test
    @DisplayName("Deve validar com sucesso quando o produto tem todos os campos válidos")
    void deveValidarComSucesso_quandoProdutoEhValido() {

        ProdutoRequest request = new ProdutoRequest("Produto Válido", BigDecimal.TEN, 12);

        assertDoesNotThrow(() -> validator.validate(request),
                "Nenhuma exceção deveria ser lançada para um produto válido.");
    }

    @ParameterizedTest(name = "Deve lançar exceção para nome inválido: \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Deve lançar exceção quando o nome do produto é nulo ou está em branco")
    void deveLancarExcecao_quandoNomeEhNuloOuEmBranco(String nomeInvalido) {

        ProdutoRequest request = new ProdutoRequest(nomeInvalido, BigDecimal.TEN, 12);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validate(request));

        assertEquals("O nome do produto é obrigatório.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a taxa de juros é nula")
    void deveLancarExcecao_quandoTaxaJurosEhNula() {

        ProdutoRequest request = new ProdutoRequest("Produto Teste", null, 12);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validate(request));

        assertEquals("A taxa de juros é obrigatória.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a taxa de juros é negativa")
    void deveLancarExcecao_quandoTaxaJurosEhNegativa() {

        ProdutoRequest request = new ProdutoRequest("Produto Teste", new BigDecimal("-0.1"), 12);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validate(request));

        assertEquals("A taxa de juros deve ser maior ou igual a zero.", exception.getMessage());
    }

    @ParameterizedTest(name = "Deve lançar exceção para prazo inválido: {0}")
    @ValueSource(ints = {0, -1, -12})
    @DisplayName("Deve lançar exceção quando o prazo máximo não é um número positivo")
    void deveLancarExcecao_quandoPrazoMaximoNaoEhPositivo(Integer prazoInvalido) {

        ProdutoRequest request = new ProdutoRequest("Produto Teste", BigDecimal.TEN, prazoInvalido);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validate(request));

        assertEquals("O prazo máximo deve ser maior que zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o prazo máximo é nulo")
    void deveLancarExcecao_quandoPrazoMaximoEhNulo() {

        ProdutoRequest request = new ProdutoRequest("Produto Teste", BigDecimal.TEN, null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validate(request));

        assertEquals("O prazo máximo é obrigatório.", exception.getMessage());
    }
}