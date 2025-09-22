package caixaverso.controller;

import caixaverso.dto.ProdutoRequest;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.service.ProdutoService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@QuarkusTest
class ProdutoControllerTest {

    @Inject
    ProdutoController controller;

    @InjectMock
    ProdutoService produtoService;

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
    @DisplayName("Deve listar todos os produtos quando a lista não está vazia")
    void deveListarTodosProdutos() {

        List<ProdutoEmprestimo> listaProdutos = List.of(produto);
        Mockito.when(produtoService.listarProdutos()).thenReturn(listaProdutos);

        List<ProdutoEmprestimo> result = controller.listar();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produto, result.get(0));
        Mockito.verify(produtoService).listarProdutos();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há produtos cadastrados")
    void deveRetornarListaVazia_quandoNaoExistemProdutos() {

        Mockito.when(produtoService.listarProdutos()).thenReturn(Collections.emptyList());

        List<ProdutoEmprestimo> result = controller.listar();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        Mockito.verify(produtoService).listarProdutos();
    }

    @Test
    @DisplayName("Deve listar um produto por ID quando encontrado")
    void deveListarPorId_quandoEncontrado() {

        Mockito.when(produtoService.listarPorId(1L)).thenReturn(produto);

        ProdutoEmprestimo result = controller.listarPorId(1L);

        assertNotNull(result);
        assertEquals(produto.getId(), result.getId());
        Mockito.verify(produtoService).listarPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar um produto por ID inexistente")
    void deveLancarNotFound_quandoListarPorIdNaoEncontrado() {

        Mockito.when(produtoService.listarPorId(anyLong())).thenThrow(new NotFoundException("Produto não encontrado"));

        assertThrows(NotFoundException.class, () -> controller.listarPorId(99L));
        Mockito.verify(produtoService).listarPorId(99L);
    }

    @Test
    @DisplayName("Deve cadastrar um novo produto com sucesso")
    void deveCadastrarProduto() {

        Mockito.when(produtoService.cadastrar(any(ProdutoRequest.class))).thenReturn(produto);

        ProdutoEmprestimo result = controller.cadastrar(produtoRequest);

        assertNotNull(result);
        assertEquals(produto.getId(), result.getId());
        Mockito.verify(produtoService).cadastrar(produtoRequest);
    }

    @Test
    @DisplayName("Deve atualizar um produto existente com sucesso")
    void deveAtualizarProduto() {

        Mockito.when(produtoService.atualizar(anyLong(), any(ProdutoRequest.class))).thenReturn(produto);

        ProdutoEmprestimo result = controller.atualizar(1L, produtoRequest);

        assertNotNull(result);
        assertEquals(produto.getId(), result.getId());
        Mockito.verify(produtoService).atualizar(1L, produtoRequest);
    }

    @Test
    @DisplayName("Deve deletar um produto existente com sucesso")
    void deveDeletarProduto() {

        doNothing().when(produtoService).deletar(anyLong());

        assertDoesNotThrow(() -> controller.deletar(1L));
        Mockito.verify(produtoService).deletar(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar deletar um produto inexistente")
    void deveLancarNotFound_quandoDeletarProdutoNaoExistente() {

        doThrow(new NotFoundException("Produto não encontrado")).when(produtoService).deletar(anyLong());

        assertThrows(NotFoundException.class, () -> controller.deletar(99L));
        Mockito.verify(produtoService).deletar(99L);
    }
}