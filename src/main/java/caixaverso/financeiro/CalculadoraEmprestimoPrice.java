package caixaverso.financeiro;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CalculadoraEmprestimoPrice {

    private static final int FINAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public ResultadoCalculoEmprestimo calcular(BigDecimal valorEmprestimo, BigDecimal taxaJurosMensal, int prazoMeses) {
        BigDecimal valorParcela = calcularValorParcela(valorEmprestimo, taxaJurosMensal, prazoMeses);
        List<ParcelaAmortizacao> memoriaCalculo = gerarMemoriaCalculo(valorEmprestimo, taxaJurosMensal, prazoMeses, valorParcela);

        // Recalcula o valor total a partir da soma das parcelas para maior precisão
        BigDecimal valorTotal = memoriaCalculo.stream()
                .map(p -> p.valorAmortizacao().add(p.valorJuros()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(FINAL_SCALE, ROUNDING_MODE);

        return new ResultadoCalculoEmprestimo(valorParcela, valorTotal, memoriaCalculo);
    }

    private BigDecimal calcularValorParcela(BigDecimal valorPresente, BigDecimal taxa, int numeroPeriodos) {
        // Trata o caso de juros zero
        if (taxa.compareTo(BigDecimal.ZERO) == 0) {
            if (numeroPeriodos <= 0) return BigDecimal.ZERO;
            return valorPresente.divide(BigDecimal.valueOf(numeroPeriodos), FINAL_SCALE, ROUNDING_MODE);
        }

        // Fórmula da Tabela Price: PMT = PV * [i * (1+i)^n] / [(1+i)^n - 1]
        BigDecimal fator = BigDecimal.ONE.add(taxa).pow(numeroPeriodos);
        BigDecimal numerador = valorPresente.multiply(taxa).multiply(fator);
        BigDecimal denominador = fator.subtract(BigDecimal.ONE);

        return numerador.divide(denominador, FINAL_SCALE, ROUNDING_MODE);
    }

    private List<ParcelaAmortizacao> gerarMemoriaCalculo(BigDecimal valorEmprestimo, BigDecimal taxaJurosMensal, int prazoMeses, BigDecimal valorParcela) {
        List<ParcelaAmortizacao> memoria = new ArrayList<>();
        BigDecimal saldoDevedor = valorEmprestimo;

        for (int mes = 1; mes <= prazoMeses; mes++) {
            BigDecimal jurosDoMes = saldoDevedor.multiply(taxaJurosMensal).setScale(FINAL_SCALE, ROUNDING_MODE);
            
            // Na última parcela, a amortização é o saldo devedor para zerar a dívida e evitar erros de arredondamento
            BigDecimal amortizacaoDoMes = (mes == prazoMeses)
                    ? saldoDevedor
                    : valorParcela.subtract(jurosDoMes);

            saldoDevedor = saldoDevedor.subtract(amortizacaoDoMes);

            memoria.add(new ParcelaAmortizacao(mes, amortizacaoDoMes, jurosDoMes, saldoDevedor.max(BigDecimal.ZERO)));
        }
        return memoria;
    }
}