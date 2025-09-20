package caixaverso.controller;

import caixaverso.model.ProdutoEmprestimo;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@QuarkusTest
class ProdutoControllerTest {

    @Inject
    EntityManager em;

    ProdutoController controller;

    @BeforeEach
    void setup() {
        controller = new ProdutoController(em);
    }

    @Test
    @Transactional
    void deveCadastrarProdutoValido() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Empréstimo Pessoal", new BigDecimal("10.5"), 36);
        produto.setId(1L);

        ProdutoEmprestimo salvo = controller.cadastrar(produto);

        assertNotNull(salvo);
        assertEquals("Empréstimo Pessoal", salvo.getNome());
    }

    @Test
    void deveLancarInternalServerErrorAoFalharPersistencia() {
        var entityManager = mock(jakarta.persistence.EntityManager.class);

        ProdutoEmprestimo produto = new ProdutoEmprestimo("Falha", new BigDecimal("10.0"), 12);
        produto.setId(999L);

        doThrow(new RuntimeException("Falha simulada")).when(entityManager).persist(produto);

        InternalServerErrorException ex = assertThrows(InternalServerErrorException.class, () -> controller.cadastrar(produto));
        assertTrue(ex.getMessage().contains("Erro ao cadastrar produto"));
    }

    @Test
    @Transactional
    void deveLancarExcecaoAoCadastrarProdutoInvalido() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("", new BigDecimal("-5.0"), 0);
        produto.setId(2L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> controller.cadastrar(produto));
        assertTrue(ex.getMessage().contains("O nome do produto é obrigatório"));
    }

    @Test
    @Transactional
    void deveListarTodosProdutos() {
        ProdutoEmprestimo p1 = new ProdutoEmprestimo("Produto A", new BigDecimal("8.0"), 24);
        ProdutoEmprestimo p2 = new ProdutoEmprestimo("Produto B", new BigDecimal("12.0"), 48);
        p1.setId(3L);
        p2.setId(4L);
        em.persist(p1);
        em.persist(p2);

        List<ProdutoEmprestimo> produtos = controller.listar();
        assertTrue(produtos.size() >= 2);
    }

    @Test
    @Transactional
    void deveBuscarProdutoPorId() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Produto C", new BigDecimal("9.0"), 36);
        produto.setId(5L);
        em.persist(produto);

        ProdutoEmprestimo encontrado = controller.listarPorId(5L);
        assertEquals("Produto C", encontrado.getNome());
    }

    @Test
    void deveLancarExcecaoAoBuscarProdutoInexistente() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> controller.listarPorId(999L));
        assertEquals("Produto não encontrado", ex.getMessage());
    }

    @Test
    @Transactional
    void deveAtualizarProdutoExistente() {
        ProdutoEmprestimo original = new ProdutoEmprestimo("Produto D", new BigDecimal("11.0"), 24);
        original.setId(6L);
        em.persist(original);

        ProdutoEmprestimo atualizado = new ProdutoEmprestimo("Produto D Atualizado", new BigDecimal("13.0"), 36);
        ProdutoEmprestimo resultado = controller.atualizar(6L, atualizado);

        assertEquals("Produto D Atualizado", resultado.getNome());
        assertEquals(36, resultado.getPrazoMaximoMeses());
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Produto X", new BigDecimal("10.0"), 12);
        NotFoundException ex = assertThrows(NotFoundException.class, () -> controller.atualizar(999L, produto));
        assertEquals("Produto não encontrado", ex.getMessage());
    }

    @Test
    @Transactional
    void deveDeletarProdutoExistente() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Produto E", new BigDecimal("7.5"), 18);
        produto.setId(7L);
        em.persist(produto);

        controller.deletar(7L);
        ProdutoEmprestimo excluido = em.find(ProdutoEmprestimo.class, 7L);
        assertNull(excluido);
    }

    @Test
    void deveLancarExcecaoAoDeletarProdutoInexistente() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> controller.deletar(999L));
        assertEquals("Produto não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoSeNomeForNuloOuVazio() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("", new BigDecimal("10.0"), 12);
        produto.setId(1L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> controller.cadastrar(produto));
        assertEquals("O nome do produto é obrigatório.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoSeTaxaForNegativa() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Produto Inválido", new BigDecimal("-1.0"), 12);
        produto.setId(2L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> controller.cadastrar(produto));
        assertEquals("A taxa de juros deve ser maior ou igual a zero.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoSePrazoForZeroOuNegativo() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Produto Inválido", new BigDecimal("10.0"), 0);
        produto.setId(3L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> controller.cadastrar(produto));
        assertEquals("O prazo máximo deve ser maior que zero.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoSeNomeForNull() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo(null, new BigDecimal("10.0"), 12);
        produto.setId(10L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> controller.cadastrar(produto));
        assertEquals("O nome do produto é obrigatório.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoSeTaxaForNull() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Produto Inválido", null, 12);
        produto.setId(11L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> controller.cadastrar(produto));
        assertEquals("A taxa de juros deve ser maior ou igual a zero.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoSePrazoForNull() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Produto Inválido", new BigDecimal("10.0"), null);
        produto.setId(12L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> controller.cadastrar(produto));
        assertEquals("O prazo máximo deve ser maior que zero.", ex.getMessage());
    }

}
