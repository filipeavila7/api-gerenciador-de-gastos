package com.example.gerenciador.transaction.dto;

import com.example.gerenciador.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long familyId,
        String title,
        BigDecimal ammount,
        TransactionType type,
        LocalDateTime dateTime

) {
}
