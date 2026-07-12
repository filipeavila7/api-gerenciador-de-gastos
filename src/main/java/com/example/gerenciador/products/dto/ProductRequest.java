package com.example.gerenciador.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 1, max = 30, message = "Nome deve ter entre 1 e 30 caracteres")
        String name,

        @NotNull
        Long categoryId
) {
}
