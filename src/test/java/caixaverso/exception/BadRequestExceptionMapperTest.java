package caixaverso.exception;

import caixaverso.dto.ErroResponse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionMapperTest {

    @Test
    @DisplayName("Deve mapear BadRequestException para uma Response com status 400 e corpo ErroResponse")
    void deveMapearBadRequestExceptionParaResponseCorreta() {

        var mapper = new BadRequestExceptionMapper();
        String mensagemErro = "O campo 'nome' é obrigatório.";
        var exception = new BadRequestException(mensagemErro);

        Response response = mapper.toResponse(exception);

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        assertInstanceOf(ErroResponse.class, response.getEntity());
        var erroResponse = (ErroResponse) response.getEntity();

        assertEquals("Requisição inválida", erroResponse.tipo());
        assertEquals(mensagemErro, erroResponse.mensagem());
    }
}