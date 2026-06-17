package com.example.gerenciador.products.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductDeleteRequest(
        @NotNull
        @NotEmpty
        List<Long> ids
) {
}
