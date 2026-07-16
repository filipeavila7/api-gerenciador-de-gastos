package com.example.gerenciador.products.dto;

import java.math.BigDecimal;

public record ProductExpenseResponse(
        String productName,
        BigDecimal total
) {
}
