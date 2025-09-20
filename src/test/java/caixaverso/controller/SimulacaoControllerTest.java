package caixaverso.controller;

import caixaverso.model.ProdutoEmprestimo;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class SimulacaoControllerTest {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void setup() {
        ProdutoEmprestimo produto = new ProdutoEmprestimo("Teste REST", new BigDecimal("15.0"), 36);
        produto.setId(500L);
        em.merge(produto);
    }

    @Test
    void deveRetornarSimulacaoValida() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "idProduto": 500,
                    "valorSolicitado": 10000,
                    "prazoMeses": 12
                }
            """)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(200)
                .body("valorSolicitado", equalTo("10000.00"))
                .body("prazoMeses", equalTo(12))
                .body("memoriaCalculo.size()", equalTo(12));
    }

    @Test
    void deveRetornarErroParaProdutoInexistente() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "idProduto": 999,
                    "valorSolicitado": 10000,
                    "prazoMeses": 12
                }
            """)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("Produto de empréstimo não encontrado para o ID informado"));

    }

    @Test
    void deveRetornarBadRequestQuandoProdutoNaoExiste() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "idProduto": 9999,
                    "valorSolicitado": 5000,
                    "prazoMeses": 12
                }
            """)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(400)
                .body("tipo", equalTo("Erro de validação"))
                .body("mensagem", containsString("Produto de empréstimo não encontrado"));
    }


}
