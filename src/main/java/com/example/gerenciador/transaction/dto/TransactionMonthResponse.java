package com.example.gerenciador.transaction.dto;

import java.math.BigDecimal;

public record TransactionMonthResponse(
        Integer month,
        BigDecimal total
) {
}
