package caixaverso.validator;

import caixaverso.dto.ProdutoRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.math.BigDecimal;

@ApplicationScoped
public class ProdutoValidator {

    public void validate(ProdutoRequest produto) {
        if (produto.nome() == null || produto.nome().isBlank()) {
            throw new BadRequestException("O nome do produto é obrigatório.");
        }
        if (produto.taxaJurosAnual() == null || produto.taxaJurosAnual().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("A taxa de juros deve ser maior ou igual a zero.");
        }
        if (produto.prazoMaximoMeses() == null || produto.prazoMaximoMeses() <= 0) {
            throw new BadRequestException("O prazo máximo deve ser maior que zero.");
        }
    }
}
