package com.example.gerenciador.products.dto;

public record ProductUpdateRequest(
        String name,
        Long categoryId
) {
}
