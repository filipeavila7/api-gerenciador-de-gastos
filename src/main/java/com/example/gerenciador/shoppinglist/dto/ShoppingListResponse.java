package com.example.gerenciador.shoppinglist.dto;

import java.time.LocalDateTime;

public record ShoppingListResponse(
        Long id,
        Long familyId,
        String name,
        LocalDateTime createdAt,
        int totalItems
) {
}
