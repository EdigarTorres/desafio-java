package caixaverso.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErroResponseTest {

    @Test
    @DisplayName("Deve criar ErroResponse e verificar os valores dos acessores")
    void deveCriarComValoresCorretos() {

        String tipo = "VALIDACAO";
        String mensagem = "Campo obrigatório não preenchido.";

        var erroResponse = new ErroResponse(tipo, mensagem);

        assertNotNull(erroResponse);
        assertEquals(tipo, erroResponse.tipo());
        assertEquals(mensagem, erroResponse.mensagem());
    }

    @Test
    @DisplayName("Deve verificar a igualdade e o hashCode entre duas instâncias")
    void deveVerificarIgualdadeEHashCode() {

        var erro1 = new ErroResponse("ERRO_INTERNO", "Falha no sistema.");
        var erro2 = new ErroResponse("ERRO_INTERNO", "Falha no sistema.");
        var erroDiferente = new ErroResponse("VALIDACAO", "Campo inválido.");

        assertEquals(erro1, erro2, "Dois records com os mesmos valores devem ser iguais.");
        assertEquals(erro1.hashCode(), erro2.hashCode(), "O hashCode de dois records iguais deve ser o mesmo.");
        assertNotEquals(erro1, erroDiferente, "Dois records com valores diferentes não devem ser iguais.");
        assertNotEquals(erro1.hashCode(), erroDiferente.hashCode(), "O hashCode de dois records diferentes não deve ser o mesmo.");
    }
}