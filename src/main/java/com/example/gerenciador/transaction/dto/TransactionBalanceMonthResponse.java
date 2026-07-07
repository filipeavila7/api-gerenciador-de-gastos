package com.example.gerenciador.transaction.dto;

import java.math.BigDecimal;

public record TransactionBalanceMonthResponse(
        BigDecimal income,
        BigDecimal expense
) {}
