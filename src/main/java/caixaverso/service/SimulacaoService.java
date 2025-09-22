package caixaverso.service;

import caixaverso.dto.SimulacaoRequest;
import caixaverso.dto.SimulacaoResponse;
import caixaverso.dto.SimulacaoResponse.ParcelaDetalhe;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.validator.SimulacaoValidator;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {

    private final SimulacaoValidator simulacaoValidator;

    public SimulacaoService(SimulacaoValidator simulacaoValidator) {
        this.simulacaoValidator = simulacaoValidator;
    }

    public SimulacaoResponse simular(SimulacaoRequest request) {

        ProdutoEmprestimo produto = simulacaoValidator.validateAndGetProduto(request);

        BigDecimal taxaAnual = produto.getTaxaJurosAnual().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal taxaMensal = BigDecimal.valueOf(Math.pow(1 + taxaAnual.doubleValue(), 1.0 / 12) - 1)
                .setScale(10, RoundingMode.HALF_UP);
        BigDecimal valorSolicitado = BigDecimal.valueOf(request.valorSolicitado());
        int prazoMeses = request.prazoMeses();

        BigDecimal valorParcela = calcularValorParcela(valorSolicitado, taxaMensal, prazoMeses);
        List<ParcelaDetalhe> detalhamentoParcelas = gerarMemoriaCalculo(valorSolicitado, taxaMensal, prazoMeses, valorParcela);

        BigDecimal valorTotal = valorParcela.multiply(BigDecimal.valueOf(prazoMeses)).setScale(2, RoundingMode.HALF_UP);

        return new SimulacaoResponse(
                produto,
                format(valorSolicitado),
                prazoMeses,
                format(produto.getTaxaJurosAnual()),
                taxaMensal.setScale(6, RoundingMode.HALF_UP).toPlainString(),
                format(valorTotal),
                format(valorParcela),
                detalhamentoParcelas
        );
    }

    private BigDecimal calcularValorParcela(BigDecimal valorPresente, BigDecimal taxa, int numeroPeriodos) {
        BigDecimal numerador = taxa.multiply(valorPresente);
        BigDecimal denominador = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(
                BigDecimal.valueOf(Math.pow(1 + taxa.doubleValue(), numeroPeriodos)), 10, RoundingMode.HALF_UP));
        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }

    private List<ParcelaDetalhe> gerarMemoriaCalculo(BigDecimal valorEmprestimo, BigDecimal taxaJurosMensal, int prazoMeses, BigDecimal valorParcela) {
        List<ParcelaDetalhe> detalhamentoParcelas = new ArrayList<>();
        BigDecimal saldoDevedor = valorEmprestimo;

        for (int mes = 1; mes <= prazoMeses; mes++) {
            BigDecimal jurosDoMes = saldoDevedor.multiply(taxaJurosMensal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacaoDoMes = valorParcela.subtract(jurosDoMes).setScale(2, RoundingMode.HALF_UP);
            saldoDevedor = saldoDevedor.subtract(amortizacaoDoMes).setScale(2, RoundingMode.HALF_UP);

            detalhamentoParcelas.add(new ParcelaDetalhe(
                    mes,
                    format(amortizacaoDoMes),
                    format(jurosDoMes),
                    format(saldoDevedor.max(BigDecimal.ZERO))
            ));
        }

        return detalhamentoParcelas;
    }

    private String format(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
