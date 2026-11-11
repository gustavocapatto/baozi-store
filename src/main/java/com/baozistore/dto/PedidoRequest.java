package com.baozistore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PedidoRequest(
        @NotNull(message = "O ID do cliente é obrigatório") Long clienteId,
        @NotNull(message = "O ID do produto é obrigatório") Long produtoId,
        @NotNull(message = "A quantidade é obrigatória") @Min(value = 1, message = "A quantidade mínima é 1") Integer quantidade
) {
}
