package com.example.gerenciador.purchase.dto;

import java.math.BigDecimal;

public record PurchaseItensResponse(
        Long purchaseId,
        Long familyId,
        Long productId,
        String productName,
        String categoryName,
        BigDecimal unitPrice,
        Long quantity,
        BigDecimal subtotal

) {
}
