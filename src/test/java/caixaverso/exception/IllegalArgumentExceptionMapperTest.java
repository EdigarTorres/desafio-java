package caixaverso.exception;

import caixaverso.dto.ErroResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IllegalArgumentExceptionMapperTest {

    @Test
    @DisplayName("Deve mapear IllegalArgumentException para uma Response com status 400 e corpo ErroResponse")
    void deveMapearIllegalArgumentExceptionParaResponseCorreta() {

        var mapper = new IllegalArgumentExceptionMapper();
        String mensagemErro = "Produto de empréstimo não encontrado.";
        var exception = new IllegalArgumentException(mensagemErro);

        Response response = mapper.toResponse(exception);

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        assertInstanceOf(ErroResponse.class, response.getEntity());
        var erroResponse = (ErroResponse) response.getEntity();

        assertEquals("Erro de validação", erroResponse.tipo());
        assertEquals(mensagemErro, erroResponse.mensagem());
    }
}