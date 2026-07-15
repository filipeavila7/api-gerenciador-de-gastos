package com.example.gerenciador.purchase.dto;

import jakarta.validation.constraints.Size;

public record PurchaseTransactionRequest(
        @Size(max = 120)
        String description
) {
}
