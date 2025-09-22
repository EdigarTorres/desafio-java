package caixaverso.dao;

import caixaverso.model.ProdutoEmprestimo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class ProdutoDao {

    private final EntityManager entityManager;

    public ProdutoDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<ProdutoEmprestimo> listarProdutos() {
        return entityManager.createQuery("FROM ProdutoEmprestimo", ProdutoEmprestimo.class).getResultList();
    }

    public ProdutoEmprestimo listarPorId(Long id) {
        return entityManager.find(ProdutoEmprestimo.class, id);

    }

    public ProdutoEmprestimo cadastrar(ProdutoEmprestimo produto) {
        entityManager.persist(produto);
        return produto;
    }

    public ProdutoEmprestimo atualizar(ProdutoEmprestimo produto) {
        entityManager.merge(produto);
        return produto;
    }

    public void deletar(ProdutoEmprestimo produto) {
        entityManager.remove(produto);
    }
}