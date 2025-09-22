package caixaverso.dto;

public record SimulacaoRequest(
        Long idProduto,
        Double valorSolicitado,
        Integer prazoMeses
) {}