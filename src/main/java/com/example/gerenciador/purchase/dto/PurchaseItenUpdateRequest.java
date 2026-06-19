package com.example.gerenciador.purchase.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PurchaseItenUpdateRequest(


        @PositiveOrZero
        BigDecimal unitPrice,

        @Positive
        Long quantity
) {
}
