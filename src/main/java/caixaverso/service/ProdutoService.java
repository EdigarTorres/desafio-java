package caixaverso.service;

import caixaverso.dao.ProdutoDao;
import caixaverso.dto.ProdutoRequest;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.validator.ProdutoValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class ProdutoService {

    private final ProdutoValidator produtoValidator;
    private final ProdutoDao produtoDao;

    public ProdutoService(ProdutoValidator produtoValidator, ProdutoDao produtoDao) {
        this.produtoValidator = produtoValidator;
        this.produtoDao = produtoDao;

    }

    public List<ProdutoEmprestimo> listarProdutos() {
        return produtoDao.listarProdutos();
    }

    public ProdutoEmprestimo listarPorId(Long id) {
        ProdutoEmprestimo produto = produtoDao.listarPorId(id);
        if (produto == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        return produto;
    }

    public ProdutoEmprestimo cadastrar(ProdutoRequest request) {
        produtoValidator.validate(request);
        ProdutoEmprestimo produto = new ProdutoEmprestimo();
        produto.setNome(request.nome());
        produto.setTaxaJurosAnual(request.taxaJurosAnual());
        produto.setPrazoMaximoMeses(request.prazoMaximoMeses());
        return produtoDao.cadastrar(produto);
    }

    public ProdutoEmprestimo atualizar(Long id, ProdutoRequest request) {
        ProdutoEmprestimo existente = produtoDao.listarPorId(id);
        if (existente == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        produtoValidator.validate(request);
        existente.setNome(request.nome());
        existente.setTaxaJurosAnual(request.taxaJurosAnual());
        existente.setPrazoMaximoMeses(request.prazoMaximoMeses());
        return produtoDao.atualizar(existente);
    }

    public void deletar(Long id) {
        ProdutoEmprestimo existente = produtoDao.listarPorId(id);
        if (existente == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        produtoDao.deletar(existente);
    }
}