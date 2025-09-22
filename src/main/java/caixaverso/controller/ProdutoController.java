package caixaverso.controller;

import caixaverso.dto.ProdutoRequest;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.service.ProdutoService;
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


    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GET
    @Operation(summary = "Lista todos os produtos de empréstimo cadastrados",
               description = "Retorna uma lista de todos os produtos de empréstimo cadastrados.")
    public List<ProdutoEmprestimo> listar() {
        return produtoService.listarProdutos();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca um produto de empréstimo por ID",
               description = "Retorna um produto de empréstimo pelo seu ID.")
    public ProdutoEmprestimo listarPorId(@PathParam("id") Long id) {
        return produtoService.listarPorId(id);
    }

    @POST
    @Operation(summary = "Cadastra um novo produto de empréstimo",
               description = "Cria um novo produto de empréstimo com os dados fornecidos.")
    public ProdutoEmprestimo cadastrar(ProdutoRequest request) {
        return produtoService.cadastrar(request);
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza um produto de empréstimo",
               description = "Atualiza um produto de empréstimo existente com os dados fornecidos.")
    public ProdutoEmprestimo atualizar(@PathParam("id") Long id, ProdutoRequest request) {
        return produtoService.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deleta um produto de empréstimo",
               description = "Remove um produto de empréstimo pelo seu ID.")
    public void deletar(@PathParam("id") Long id) {
        produtoService.deletar(id);
    }
}


