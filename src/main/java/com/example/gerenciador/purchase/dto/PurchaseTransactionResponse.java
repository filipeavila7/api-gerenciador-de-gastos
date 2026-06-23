package com.example.gerenciador.purchase.dto;

import com.example.gerenciador.purchase.entity.PurchaseStatus;
import com.example.gerenciador.transaction.dto.TransactionResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseTransactionResponse(
        Long purchaseId,
        Long famillId,
        PurchaseStatus status,
        TransactionResponse transaction

) {
}
