package caixaverso.financeiro;

import java.math.BigDecimal;

public record ParcelaAmortizacao(
        int numero,
        BigDecimal valorAmortizacao,
        BigDecimal valorJuros,
        BigDecimal saldoDevedor
) {
}