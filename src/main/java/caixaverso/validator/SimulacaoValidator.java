package caixaverso.validator;

import caixaverso.dao.ProdutoDao;
import caixaverso.dto.SimulacaoRequest;
import caixaverso.model.ProdutoEmprestimo;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimulacaoValidator {

    private final ProdutoDao produtoDao;

    public SimulacaoValidator(ProdutoDao produtoDao) {
        this.produtoDao = produtoDao;
    }

    public ProdutoEmprestimo validateAndGetProduto(SimulacaoRequest request) {

        if (request.idProduto() == null) {
            throw new IllegalArgumentException("O ID do produto é obrigatório.");
        }
        if (request.valorSolicitado() == null || request.valorSolicitado() <= 0) {
            throw new IllegalArgumentException("O valor solicitado deve ser um número positivo.");
        }
        if (request.prazoMeses() == null || request.prazoMeses() <= 0) {
            throw new IllegalArgumentException("O prazo em meses deve ser um número positivo.");
        }

        ProdutoEmprestimo produto = produtoDao.listarPorId(request.idProduto());

        if (produto == null) {
            throw new IllegalArgumentException("Produto de empréstimo não encontrado para o ID informado.");
        }

        if (request.prazoMeses() > produto.getPrazoMaximoMeses()) {
            throw new IllegalArgumentException(
                    String.format("O prazo solicitado (%d meses) excede o prazo máximo do produto (%d meses).",
                            request.prazoMeses(), produto.getPrazoMaximoMeses()));
        }
        return produto;
    }
}