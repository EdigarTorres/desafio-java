package caixaverso.service;

import caixaverso.dto.SimulacaoRequest;
import caixaverso.dto.SimulacaoResponse;
import caixaverso.dto.SimulacaoResponse.ParcelaDetalhe;
import caixaverso.model.ProdutoEmprestimo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {

    private final EntityManager entityManager;

    public SimulacaoService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public SimulacaoResponse simular(SimulacaoRequest request) {
        ProdutoEmprestimo produto = entityManager.find(ProdutoEmprestimo.class, request.idProduto());
        if (produto == null) {
            throw new IllegalArgumentException("Produto de empréstimo não encontrado para o ID informado.");
        }

        if (request.prazoMeses() > produto.getPrazoMaximoMeses()) {
            throw new IllegalArgumentException(
                    String.format("O prazo solicitado (%d meses) excede o prazo máximo do produto (%d meses).",
                            request.prazoMeses(), produto.getPrazoMaximoMeses())
            );
        }

        BigDecimal taxaAnual = produto.getTaxaJurosAnual().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal taxaMensal = BigDecimal.valueOf(Math.pow(1 + taxaAnual.doubleValue(), 1.0 / 12) - 1)
                .setScale(10, RoundingMode.HALF_UP);

        BigDecimal valorSolicitado = BigDecimal.valueOf(request.valorSolicitado());
        int prazo = request.prazoMeses();

        BigDecimal parcelaFixa = calcularParcela(valorSolicitado, taxaMensal, prazo);
        List<ParcelaDetalhe> memoria = gerarMemoriaCalculo(valorSolicitado, taxaMensal, prazo, parcelaFixa);

        BigDecimal valorTotal = parcelaFixa.multiply(BigDecimal.valueOf(prazo)).setScale(2, RoundingMode.HALF_UP);

        return new SimulacaoResponse(
                produto,
                format(valorSolicitado),
                prazo,
                format(produto.getTaxaJurosAnual()),
                taxaMensal.setScale(6, RoundingMode.HALF_UP).toPlainString(),
                format(valorTotal),
                format(parcelaFixa),
                memoria
        );
    }




    private BigDecimal calcularParcela(BigDecimal pv, BigDecimal i, int n) {
        BigDecimal numerador = i.multiply(pv);
        BigDecimal denominador = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(
                BigDecimal.valueOf(Math.pow(1 + i.doubleValue(), n)), 10, RoundingMode.HALF_UP));
        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }

    private List<ParcelaDetalhe> gerarMemoriaCalculo(BigDecimal pv, BigDecimal i, int n, BigDecimal parcela) {
        List<ParcelaDetalhe> memoria = new ArrayList<>();
        BigDecimal saldo = pv;

        for (int mes = 1; mes <= n; mes++) {
            BigDecimal juros = saldo.multiply(i).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = parcela.subtract(juros).setScale(2, RoundingMode.HALF_UP);
            saldo = saldo.subtract(amortizacao).setScale(2, RoundingMode.HALF_UP);

            memoria.add(new ParcelaDetalhe(
                    mes,
                    format(amortizacao),
                    format(juros),
                    format(saldo.max(BigDecimal.ZERO))
            ));
        }

        return memoria;
    }

    private String format(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
