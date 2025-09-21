package caixaverso.controller;

import caixaverso.dto.ProdutoRequest;
import caixaverso.model.ProdutoEmprestimo;
import caixaverso.validator.ProdutoValidator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

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
    @DisplayName("Deve listar um produto por ID quando encontrado")
    void deveListarPorId_quandoEncontrado() {

        Mockito.when(entityManager.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        ProdutoEmprestimo result = controller.listarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar um produto por ID inexistente")
    void deveLancarNotFound_quandoListarPorIdNaoEncontrado() {

        Mockito.when(entityManager.find(eq(ProdutoEmprestimo.class), anyLong())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> controller.listarPorId(99L));
    }

    @Test
    @DisplayName("Deve cadastrar um novo produto com sucesso")
    void deveCadastrarProduto() {

        ProdutoEmprestimo result = controller.cadastrar(produtoRequest);

        Mockito.verify(produtoValidator).validate(produtoRequest); // Verifica se o validador foi chamado
        Mockito.verify(entityManager).persist(any(ProdutoEmprestimo.class)); // Verifica se a persistência foi chamada
        assertNotNull(result);
        assertEquals("Crédito Pessoal", result.getNome());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao tentar cadastrar um produto com dados inválidos")
    void deveLancarBadRequest_quandoCadastrarProdutoInvalido() {

        Mockito.doThrow(new BadRequestException("Erro de validação")).when(produtoValidator).validate(any(ProdutoRequest.class));

        assertThrows(BadRequestException.class, () -> controller.cadastrar(produtoRequest));
        Mockito.verify(entityManager, Mockito.never()).persist(any()); // Garante que a persistência NUNCA foi chamada
    }

    @Test
    @DisplayName("Deve atualizar um produto existente com sucesso")
    void deveAtualizarProduto() {

        ProdutoRequest requestAtualizacao = new ProdutoRequest("Nome Novo", new BigDecimal("15.0"), 36);
        Mockito.when(entityManager.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        ProdutoEmprestimo result = controller.atualizar(1L, requestAtualizacao);

        Mockito.verify(produtoValidator).validate(requestAtualizacao);
        assertNotNull(result);
        assertEquals("Nome Novo", result.getNome());
        assertEquals(36, result.getPrazoMaximoMeses());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar um produto inexistente")
    void deveLancarNotFound_quandoAtualizarProdutoNaoExistente() {

        Mockito.when(entityManager.find(eq(ProdutoEmprestimo.class), anyLong())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> controller.atualizar(99L, produtoRequest));
    }

    @Test
    @DisplayName("Deve deletar um produto existente com sucesso")
    void deveDeletarProduto() {

        Mockito.when(entityManager.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        controller.deletar(1L);

        Mockito.verify(entityManager).remove(produto);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar deletar um produto inexistente")
    void deveLancarNotFound_quandoDeletarProdutoNaoExistente() {

        Mockito.when(entityManager.find(eq(ProdutoEmprestimo.class), anyLong())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> controller.deletar(99L));
        Mockito.verify(entityManager, Mockito.never()).remove(any());
    }
}
