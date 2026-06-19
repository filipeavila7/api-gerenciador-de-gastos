package com.example.gerenciador.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseResponse(
        Long purchaseId,
        String name,
        Long familyId,
        LocalDateTime dateTime,
        BigDecimal total

) {
}
