package com.example.gerenciador.purchase.dto;

import java.util.List;

public record PurchaseManyItensRequest(
        List<PurchaseItensRequest> itensRequests
) {
}
