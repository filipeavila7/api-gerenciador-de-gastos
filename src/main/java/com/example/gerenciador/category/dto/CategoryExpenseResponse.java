package com.example.gerenciador.category.dto;

import java.math.BigDecimal;

public record CategoryExpenseResponse(
        String category,
        BigDecimal total
) {
}
