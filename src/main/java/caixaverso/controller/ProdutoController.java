package caixaverso.controller;

import caixaverso.model.ProdutoEmprestimo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.math.BigDecimal;
import java.util.List;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ProdutoController {

    private final EntityManager entityManager;

    public ProdutoController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @GET
    @Operation(summary = "Lista todos os produtos de empréstimo cadastrados")
    public List<ProdutoEmprestimo> listar() {
        return entityManager.createQuery("FROM ProdutoEmprestimo", ProdutoEmprestimo.class).getResultList();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca um produto de empréstimo por ID")
    public ProdutoEmprestimo listarPorId(@PathParam("id") Long id) {
        ProdutoEmprestimo produto = entityManager.find(ProdutoEmprestimo.class, id);
        if (produto == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        return produto;
    }

    @POST
    @Operation(summary = "Cadastra um novo produto de empréstimo")
    public ProdutoEmprestimo cadastrar(ProdutoEmprestimo produto) {
        validarProduto(produto);
        try {
            entityManager.persist(produto);
            return produto;
        } catch (Exception e) {
            throw new InternalServerErrorException("Erro ao cadastrar produto: " + e.getMessage());
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza um produto de empréstimo")
    public ProdutoEmprestimo atualizar(@PathParam("id") Long id, ProdutoEmprestimo produto) {
        ProdutoEmprestimo existente = entityManager.find(ProdutoEmprestimo.class, id);
        if (existente == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        validarProduto(produto);
        existente.setNome(produto.getNome());
        existente.setTaxaJurosAnual(produto.getTaxaJurosAnual());
        existente.setPrazoMaximoMeses(produto.getPrazoMaximoMeses());
        return existente;
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deleta um produto de empréstimo")
    public void deletar(@PathParam("id") Long id) {
        ProdutoEmprestimo produto = entityManager.find(ProdutoEmprestimo.class, id);
        if (produto == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        entityManager.remove(produto);
    }

    private void validarProduto(ProdutoEmprestimo produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new BadRequestException("O nome do produto é obrigatório.");
        }
        if (produto.getTaxaJurosAnual() == null || produto.getTaxaJurosAnual().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("A taxa de juros deve ser maior ou igual a zero.");
        }
        if (produto.getPrazoMaximoMeses() == null || produto.getPrazoMaximoMeses() <= 0) {
            throw new BadRequestException("O prazo máximo deve ser maior que zero.");
        }
    }
}
