package com.example.gerenciador.transaction.dto;

import com.example.gerenciador.purchase.dto.PurchaseItensResponse;
import com.example.gerenciador.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TransactionInfoResponse(
        Long id,
        Long familyId,
        Long purhcaseId,
        String title,
        BigDecimal ammount,
        String description,
        LocalDateTime date,
        TransactionType type,
        List<PurchaseItensResponse> items

) {
}
