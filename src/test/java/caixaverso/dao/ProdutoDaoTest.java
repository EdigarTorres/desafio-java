package caixaverso.dao;

import caixaverso.model.ProdutoEmprestimo;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
class ProdutoDaoTest {

    @Inject
    ProdutoDao produtoDao;

    @InjectMock
    EntityManager entityManager;

    private ProdutoEmprestimo produto;

    @BeforeEach
    void setUp() {
        produto = new ProdutoEmprestimo();
        produto.setId(1L);
        produto.setNome("Crédito Teste");
        produto.setTaxaJurosAnual(BigDecimal.TEN);
        produto.setPrazoMaximoMeses(12);
    }

    @Test
    @DisplayName("Deve listar um produto por ID com sucesso")
    void deveListarPorIdComSucesso() {

        Mockito.when(entityManager.find(ProdutoEmprestimo.class, 1L)).thenReturn(produto);

        ProdutoEmprestimo result = produtoDao.listarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        Mockito.verify(entityManager).find(ProdutoEmprestimo.class, 1L);
    }

    @Test
    @DisplayName("Deve retornar nulo quando um produto não é encontrado por ID")
    void deveRetornarNulo_quandoListarPorIdNaoEncontrado() {

        Mockito.when(entityManager.find(eq(ProdutoEmprestimo.class), any(Long.class))).thenReturn(null);

        ProdutoEmprestimo result = produtoDao.listarPorId(99L);

        assertNull(result);
    }

    @Test
    @DisplayName("Deve chamar o método persist ao cadastrar um produto")
    void deveCadastrarComSucesso() {

        ProdutoEmprestimo result = produtoDao.cadastrar(produto);

        Mockito.verify(entityManager).persist(produto);
        assertEquals(produto, result);
    }

    @Test
    @DisplayName("Deve chamar o método merge ao atualizar um produto")
    void deveAtualizarComSucesso() {

        ProdutoEmprestimo result = produtoDao.atualizar(produto);

        Mockito.verify(entityManager).merge(produto);
        assertEquals(produto, result);
    }

    @Test
    @DisplayName("Deve chamar o método remove ao deletar um produto")
    void deveDeletarComSucesso() {

        produtoDao.deletar(produto);

        Mockito.verify(entityManager).remove(produto);
    }
}