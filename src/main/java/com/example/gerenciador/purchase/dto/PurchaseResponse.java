package com.example.gerenciador.purchase.dto;

import com.example.gerenciador.purchase.entity.PurchaseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseResponse(
        Long purchaseId,
        String name,
        Long familyId,
        LocalDateTime dateTime,
        PurchaseStatus status,
        int quantityProducts,
        BigDecimal total

) {
}
