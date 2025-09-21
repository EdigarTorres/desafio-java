package caixaverso.controller;

import caixaverso.dto.ProdutoRequest;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.validator.ProdutoValidator;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.List;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ProdutoController {

    private final EntityManager entityManager;
    private final ProdutoValidator produtoValidator;

    public ProdutoController(EntityManager entityManager, ProdutoValidator produtoValidator) {
        this.entityManager = entityManager;
        this.produtoValidator = produtoValidator;
    }

    @GET
    @Operation(summary = "Lista todos os produtos de empréstimo cadastrados",
               description = "Retorna uma lista de todos os produtos de empréstimo cadastrados.")
    public List<ProdutoEmprestimo> listar() {
        return entityManager.createQuery("FROM ProdutoEmprestimo", ProdutoEmprestimo.class).getResultList();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca um produto de empréstimo por ID",
               description = "Retorna um produto de empréstimo pelo seu ID.")
    public ProdutoEmprestimo listarPorId(@PathParam("id") Long id) {
        ProdutoEmprestimo produto = entityManager.find(ProdutoEmprestimo.class, id);
        if (produto == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        return produto;
    }

    @POST
    @Operation(summary = "Cadastra um novo produto de empréstimo",
               description = "Cria um novo produto de empréstimo com os dados fornecidos.")
    public ProdutoEmprestimo cadastrar(ProdutoRequest request) {
        produtoValidator.validate(request);
        ProdutoEmprestimo produto = new ProdutoEmprestimo();
        produto.setNome(request.nome());
        produto.setTaxaJurosAnual(request.taxaJurosAnual());
        produto.setPrazoMaximoMeses(request.prazoMaximoMeses());
        entityManager.persist(produto);
        return produto;
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza um produto de empréstimo",
               description = "Atualiza um produto de empréstimo existente com os dados fornecidos.")
    public ProdutoEmprestimo atualizar(@PathParam("id") Long id, ProdutoRequest request) {
        ProdutoEmprestimo existente = entityManager.find(ProdutoEmprestimo.class, id);
        if (existente == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        produtoValidator.validate(request);
        existente.setNome(request.nome());
        existente.setTaxaJurosAnual(request.taxaJurosAnual());
        existente.setPrazoMaximoMeses(request.prazoMaximoMeses());
        return existente;
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deleta um produto de empréstimo",
               description = "Remove um produto de empréstimo pelo seu ID.")
    public void deletar(@PathParam("id") Long id) {
        ProdutoEmprestimo produto = entityManager.find(ProdutoEmprestimo.class, id);
        if (produto == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        entityManager.remove(produto);
    }
}
