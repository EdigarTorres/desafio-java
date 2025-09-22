package caixaverso.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SimulacaoRequest(
        @NotNull Long idProduto,
        @NotNull @Min(1) Double valorSolicitado,
        @NotNull @Min(1) Integer prazoMeses
) {}