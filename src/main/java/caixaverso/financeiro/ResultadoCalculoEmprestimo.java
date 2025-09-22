package caixaverso.financeiro;

import java.math.BigDecimal;
import java.util.List;

public record ResultadoCalculoEmprestimo(
        BigDecimal valorParcela,
        BigDecimal valorTotal,
        List<ParcelaAmortizacao> memoriaCalculo
) {
}