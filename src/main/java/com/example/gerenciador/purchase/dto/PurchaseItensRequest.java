package com.example.gerenciador.purchase.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PurchaseItensRequest(

        @NotNull
        @Positive
        Long productId,

        @NotNull
        @PositiveOrZero
        BigDecimal unitPrice,

        @NotNull
        @Positive
        Long quantity,

        @Positive
        BigDecimal discount
) {
}
