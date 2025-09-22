package caixaverso.exception;

import caixaverso.dto.ErroResponse;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionMapperTest {

    @Test
    @DisplayName("Deve mapear NotFoundException para uma Response com status 404 e corpo ErroResponse")
    void deveMapearNotFoundExceptionParaResponseCorreta() {

        var mapper = new NotFoundExceptionMapper();
        String mensagemErro = "Produto com ID 99 não foi encontrado.";
        var exception = new NotFoundException(mensagemErro);

        Response response = mapper.toResponse(exception);

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        assertInstanceOf(ErroResponse.class, response.getEntity());
        var erroResponse = (ErroResponse) response.getEntity();

        assertEquals("Recurso não encontrado", erroResponse.tipo());
        assertEquals(mensagemErro, erroResponse.mensagem());
    }
}