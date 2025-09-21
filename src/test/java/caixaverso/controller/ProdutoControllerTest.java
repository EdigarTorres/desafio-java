package caixaverso.controller;

import caixaverso.dto.ProdutoRequest;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.validator.ProdutoValidator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@QuarkusTest
class ProdutoControllerTest {

    @Inject
    ProdutoController controller;

    @InjectMock
    EntityManager entityManager;

    @InjectMock
    ProdutoValidator produtoValidator;

    private ProdutoEmprestimo produto;
    private ProdutoRequest produtoRequest;

    @BeforeEach
    void setup() {
        produto = new ProdutoEmprestimo("Crédito Pessoal", new BigDecimal("12.5"), 24);
        produto.setId(1L);

        produtoRequest = new ProdutoRequest("Crédito Pessoal", new BigDecimal("12.5"), 24);
    }

    @Test
    void deveListarTodosProdutos() {
        // Arrange
        TypedQuery<ProdutoEmprestimo> query = (TypedQuery<ProdutoEmprestimo>) Mockito.mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(anyString(), Mockito.eq(ProdutoEmprestimo.class))).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(Collections.singletonList(produto));

        // Act
        List<ProdutoEmprestimo> result = controller.listar();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void deveListarPorId_quandoEncontrado() {
        // Arrange
        Mockito.when(entityManager.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        // Act
        ProdutoEmprestimo result = controller.listarPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void deveLancarNotFound_quandoListarPorIdNaoEncontrado() {
        // Arrange
        Mockito.when(entityManager.find(ProdutoEmprestimo.class, anyLong())).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> controller.listarPorId(99L));
    }

    @Test
    void deveCadastrarProduto() {
        // Act
        ProdutoEmprestimo result = controller.cadastrar(produtoRequest);

        // Assert
        Mockito.verify(produtoValidator).validate(produtoRequest); // Verifica se o validador foi chamado
        Mockito.verify(entityManager).persist(any(ProdutoEmprestimo.class)); // Verifica se a persistência foi chamada
        assertNotNull(result);
        assertEquals("Crédito Pessoal", result.getNome());
    }

    @Test
    void deveLancarBadRequest_quandoCadastrarProdutoInvalido() {
        // Arrange
        Mockito.doThrow(new BadRequestException("Erro de validação")).when(produtoValidator).validate(any(ProdutoRequest.class));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> controller.cadastrar(produtoRequest));
        Mockito.verify(entityManager, Mockito.never()).persist(any()); // Garante que a persistência NUNCA foi chamada
    }

    @Test
    void deveAtualizarProduto() {
        // Arrange
        ProdutoRequest requestAtualizacao = new ProdutoRequest("Nome Novo", new BigDecimal("15.0"), 36);
        Mockito.when(entityManager.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        // Act
        ProdutoEmprestimo result = controller.atualizar(1L, requestAtualizacao);

        // Assert
        Mockito.verify(produtoValidator).validate(requestAtualizacao);
        assertNotNull(result);
        assertEquals("Nome Novo", result.getNome());
        assertEquals(36, result.getPrazoMaximoMeses());
    }

    @Test
    void deveLancarNotFound_quandoAtualizarProdutoNaoExistente() {
        // Arrange
        Mockito.when(entityManager.find(ProdutoEmprestimo.class, anyLong())).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> controller.atualizar(99L, produtoRequest));
    }

    @Test
    void deveDeletarProduto() {
        // Arrange
        Mockito.when(entityManager.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        // Act
        controller.deletar(1L);

        // Assert
        Mockito.verify(entityManager).remove(produto); // Verifica se o método remove foi chamado com o objeto correto
    }

    @Test
    void deveLancarNotFound_quandoDeletarProdutoNaoExistente() {
        // Arrange
        Mockito.when(entityManager.find(ProdutoEmprestimo.class, anyLong())).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> controller.deletar(99L));
        Mockito.verify(entityManager, Mockito.never()).remove(any()); // Garante que o remove NUNCA foi chamado
    }
}
