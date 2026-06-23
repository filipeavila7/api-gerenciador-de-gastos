package com.example.gerenciador.transaction.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal balance
) {
}
