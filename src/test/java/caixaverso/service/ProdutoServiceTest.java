package caixaverso.service;

import caixaverso.dao.ProdutoDao;
import caixaverso.dto.ProdutoRequest;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.validator.ProdutoValidator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@QuarkusTest
class ProdutoServiceTest {

    @Inject
    ProdutoService produtoService;

    @InjectMock
    ProdutoDao produtoDao;

    @InjectMock
    ProdutoValidator produtoValidator;

    private ProdutoEmprestimo produto;
    private ProdutoRequest produtoRequest;

    @BeforeEach
    void setUp() {
        produto = new ProdutoEmprestimo();
        produto.setId(1L);
        produto.setNome("Crédito Pessoal");
        produto.setTaxaJurosAnual(new BigDecimal("19.9"));
        produto.setPrazoMaximoMeses(24);

        produtoRequest = new ProdutoRequest(
                "Crédito Pessoal",
                new BigDecimal("19.9"),
                24
        );
    }

    @Test
    @DisplayName("Deve listar produtos com sucesso")
    void deveListarProdutos() {

        List<ProdutoEmprestimo> lista = List.of(produto);
        Mockito.when(produtoDao.listarProdutos()).thenReturn(lista);

        List<ProdutoEmprestimo> result = produtoService.listarProdutos();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(produtoDao).listarProdutos();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há produtos")
    void deveRetornarListaVazia() {

        Mockito.when(produtoDao.listarProdutos()).thenReturn(Collections.emptyList());

        List<ProdutoEmprestimo> result = produtoService.listarProdutos();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(produtoDao).listarProdutos();
    }

    @Test
    @DisplayName("Deve listar produto por ID com sucesso")
    void deveListarPorId() {

        Mockito.when(produtoDao.listarPorId(1L)).thenReturn(produto);

        ProdutoEmprestimo result = produtoService.listarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(produtoDao).listarPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar por ID inexistente")
    void deveLancarNotFoundAoListarPorId() {

        Mockito.when(produtoDao.listarPorId(anyLong())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> produtoService.listarPorId(99L));
        verify(produtoDao).listarPorId(99L);
    }

    @Test
    @DisplayName("Deve cadastrar produto com sucesso")
    void deveCadastrarProduto() {

        Mockito.when(produtoDao.cadastrar(any(ProdutoEmprestimo.class))).thenReturn(produto);

        ProdutoEmprestimo result = produtoService.cadastrar(produtoRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(produtoValidator).validate(produtoRequest);
        verify(produtoDao).cadastrar(any(ProdutoEmprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar produto inválido")
    void deveLancarExcecaoAoCadastrarProdutoInvalido() {

        Mockito.doThrow(new BadRequestException("Dados inválidos")).when(produtoValidator).validate(any(ProdutoRequest.class));

        assertThrows(BadRequestException.class, () -> produtoService.cadastrar(produtoRequest));
        verify(produtoDao, never()).cadastrar(any(ProdutoEmprestimo.class));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProduto() {

        Mockito.when(produtoDao.listarPorId(1L)).thenReturn(produto);
        Mockito.when(produtoDao.atualizar(any(ProdutoEmprestimo.class))).thenReturn(produto);

        ProdutoEmprestimo result = produtoService.atualizar(1L, produtoRequest);

        assertNotNull(result);
        verify(produtoDao).listarPorId(1L);
        verify(produtoValidator).validate(produtoRequest);
        verify(produtoDao).atualizar(produto);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar produto inexistente")
    void deveLancarNotFoundAoAtualizar() {

        Mockito.when(produtoDao.listarPorId(anyLong())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> produtoService.atualizar(99L, produtoRequest));
        verify(produtoDao).listarPorId(99L);
        verify(produtoValidator, never()).validate(any(ProdutoRequest.class));
        verify(produtoDao, never()).atualizar(any(ProdutoEmprestimo.class));
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProduto() {

        Mockito.when(produtoDao.listarPorId(1L)).thenReturn(produto);

        produtoService.deletar(1L);

        verify(produtoDao).listarPorId(1L);
        verify(produtoDao).deletar(produto);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar deletar produto inexistente")
    void deveLancarNotFoundAoDeletar() {

        Mockito.when(produtoDao.listarPorId(anyLong())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> produtoService.deletar(99L));
        verify(produtoDao).listarPorId(99L);
        verify(produtoDao, never()).deletar(any(ProdutoEmprestimo.class));
    }
}