package com.example.gerenciador.purchase.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PurchaseManyItensRequest(

        @NotEmpty
        List<PurchaseItensRequest> itensRequests
) {
}
