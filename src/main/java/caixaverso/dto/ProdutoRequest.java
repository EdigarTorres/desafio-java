package caixaverso.dto;

import java.math.BigDecimal;

public record ProdutoRequest(
    String nome,
    BigDecimal taxaJurosAnual,
    Integer prazoMaximoMeses
) {}
