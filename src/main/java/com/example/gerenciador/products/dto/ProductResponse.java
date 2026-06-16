package com.example.gerenciador.products.dto;

public record ProductResponse(
        Long id,
        String name,
        Long categoryId,
        Long familyId
) {
}
