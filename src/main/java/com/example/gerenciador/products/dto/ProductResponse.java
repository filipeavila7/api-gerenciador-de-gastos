package com.example.gerenciador.products.dto;

public record ProductResponse(
        Long id,
        String name,
        Long categoryId,
        String categoryName,
        Long familyId
) {
}
