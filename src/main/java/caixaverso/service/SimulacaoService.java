package caixaverso.service;

import caixaverso.dto.SimulacaoRequest;
import caixaverso.dto.SimulacaoResponse;
import caixaverso.dto.SimulacaoResponse.ParcelaDetalhe;
import caixaverso.financeiro.CalculadoraEmprestimoPrice;
import caixaverso.financeiro.ParcelaAmortizacao;
import caixaverso.financeiro.ResultadoCalculoEmprestimo;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.validator.SimulacaoValidator;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {

    private final SimulacaoValidator simulacaoValidator;
    private final CalculadoraEmprestimoPrice calculadora;

    public SimulacaoService(SimulacaoValidator simulacaoValidator, CalculadoraEmprestimoPrice calculadora) {
        this.simulacaoValidator = simulacaoValidator;
        this.calculadora = calculadora;
    }

    public SimulacaoResponse simular(SimulacaoRequest request) {

        ProdutoEmprestimo produto = simulacaoValidator.validateAndGetProduto(request);

        BigDecimal taxaMensal = calcularTaxaMensalEquivalente(produto.getTaxaJurosAnual());
        BigDecimal valorSolicitado = BigDecimal.valueOf(request.valorSolicitado());

        ResultadoCalculoEmprestimo resultadoCalculo = calculadora.calcular(
                valorSolicitado,
                taxaMensal,
                request.prazoMeses()
        );

        return construirResponse(produto, request, taxaMensal, resultadoCalculo);
    }

    private BigDecimal calcularTaxaMensalEquivalente(BigDecimal taxaAnual) {

        BigDecimal taxaAnualDecimal = taxaAnual.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        double base = BigDecimal.ONE.add(taxaAnualDecimal).doubleValue();
        double expoente = 1.0 / 12.0;
        double taxaMensalDouble = Math.pow(base, expoente) - 1;
        return BigDecimal.valueOf(taxaMensalDouble).setScale(10, RoundingMode.HALF_UP);
    }

    private SimulacaoResponse construirResponse(ProdutoEmprestimo produto, SimulacaoRequest request, BigDecimal taxaMensal, ResultadoCalculoEmprestimo resultadoCalculo) {
        List<ParcelaDetalhe> memoriaResponse = resultadoCalculo.memoriaCalculo().stream()
                .map(this::mapearParaParcelaDetalhe)
                .toList();

        return new SimulacaoResponse(
                produto,
                format(BigDecimal.valueOf(request.valorSolicitado())),
                request.prazoMeses(),
                format(produto.getTaxaJurosAnual()),
                taxaMensal.setScale(6, RoundingMode.HALF_UP).toPlainString(),
                format(resultadoCalculo.valorTotal()),
                format(resultadoCalculo.valorParcela()),
                memoriaResponse
        );
    }

    private ParcelaDetalhe mapearParaParcelaDetalhe(ParcelaAmortizacao parcela) {
        return new ParcelaDetalhe(
                parcela.numero(),
                format(parcela.valorAmortizacao()),
                format(parcela.valorJuros()),
                format(parcela.saldoDevedor())
        );
    }

    private String format(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
