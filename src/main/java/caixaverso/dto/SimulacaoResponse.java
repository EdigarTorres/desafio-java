package caixaverso.dto;

import caixaverso.model.ProdutoEmprestimo;

import java.util.List;

public record SimulacaoResponse(
        ProdutoEmprestimo produto,
        String valorSolicitado,
        Integer prazoMeses,
        String taxaJurosAnual,
        String taxaJurosEfetivaMensal,
        String valorTotalComJuros,
        String valorParcelaMensal,
        List<ParcelaDetalhe> memoriaCalculo
) {
    public record ParcelaDetalhe(
            int mes,
            String amortizacao,
            String juros,
            String saldoDevedor
    ) {}
}
