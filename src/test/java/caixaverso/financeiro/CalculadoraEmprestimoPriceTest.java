package caixaverso.financeiro;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class CalculadoraEmprestimoPriceTest {

    @Inject
    CalculadoraEmprestimoPrice calculadora;

    @Test
    @DisplayName("Deve calcular corretamente um empréstimo com juros")
    void deveCalcularCorretamente_comJuros() {

        BigDecimal valorEmprestimo = new BigDecimal("10000.00");

        BigDecimal taxaMensal = new BigDecimal("0.0099588018");
        int prazoMeses = 24;

        ResultadoCalculoEmprestimo resultado = calculadora.calcular(valorEmprestimo, taxaMensal, prazoMeses);

        assertNotNull(resultado);

        assertEquals(new BigDecimal("470.50"), resultado.valorParcela());

        assertEquals(new BigDecimal("11292.12"), resultado.valorTotal());
        assertEquals(24, resultado.memoriaCalculo().size());

        assertEquals(new BigDecimal("0.00"), resultado.memoriaCalculo().get(23).saldoDevedor());

        assertTrue(resultado.memoriaCalculo().get(0).valorJuros().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Deve calcular corretamente um empréstimo sem juros (taxa zero)")
    void deveCalcularCorretamente_quandoTaxaDeJurosEhZero() {

        BigDecimal valorEmprestimo = new BigDecimal("12000.00");
        BigDecimal taxaMensalZero = BigDecimal.ZERO;
        int prazoMeses = 12;

        ResultadoCalculoEmprestimo resultado = calculadora.calcular(valorEmprestimo, taxaMensalZero, prazoMeses);

        assertNotNull(resultado);

        BigDecimal parcelaEsperada = valorEmprestimo.divide(BigDecimal.valueOf(prazoMeses), 2, RoundingMode.HALF_UP);
        assertEquals(parcelaEsperada, resultado.valorParcela(), "A parcela deve ser o valor do empréstimo dividido pelo prazo."); // 12000 / 12 = 1000

        assertEquals(valorEmprestimo.setScale(2, RoundingMode.HALF_UP), resultado.valorTotal(), "O valor total deve ser igual ao valor do empréstimo.");

        assertTrue(resultado.memoriaCalculo().stream().allMatch(p -> p.valorJuros().compareTo(BigDecimal.ZERO) == 0), "Todos os juros mensais devem ser zero.");

        resultado.memoriaCalculo().forEach(p -> assertEquals(parcelaEsperada, p.valorAmortizacao(), "A amortização deve ser igual à parcela."));

        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), resultado.memoriaCalculo().get(prazoMeses - 1).saldoDevedor(), "O saldo devedor final deve ser zero.");
    }

    @Test
    @DisplayName("Deve tratar corretamente o caso de prazo zero ou negativo com taxa zero")
    void deveTratarPrazoInvalido_quandoTaxaDeJurosEhZero() {

        BigDecimal valorEmprestimo = new BigDecimal("12000.00");
        BigDecimal taxaMensalZero = BigDecimal.ZERO;
        int prazoInvalido = 0;

        ResultadoCalculoEmprestimo resultado = calculadora.calcular(valorEmprestimo, taxaMensalZero, prazoInvalido);

        assertNotNull(resultado);
        assertEquals(BigDecimal.ZERO, resultado.valorParcela());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), resultado.valorTotal());
        assertTrue(resultado.memoriaCalculo().isEmpty());
    }
}