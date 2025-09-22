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
        if (produto.taxaJurosAnual() == null) {
            throw new BadRequestException("A taxa de juros é obrigatória.");
        }
        if (produto.taxaJurosAnual().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("A taxa de juros deve ser maior ou igual a zero.");
        }
        if (produto.taxaJurosAnual().compareTo(BigDecimal.valueOf(360)) > 0){
            throw new BadRequestException("A taxa de juros deve ser menor ou igual a 360% ao ano.");
        }
        if (produto.prazoMaximoMeses() == null) {
            throw new BadRequestException("O prazo máximo é obrigatório.");
        }
        if (produto.prazoMaximoMeses() <= 0) {
            throw new BadRequestException("O prazo máximo deve ser maior que zero.");
        }
        if (produto.prazoMaximoMeses() > 360) {
            throw new BadRequestException("O prazo máximo deve ser menor ou igual a 360 meses.");
        }
    }
}
