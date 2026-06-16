package com.example.gerenciador.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 5, max = 15, message = "Nome deve ter entre 5 e 15 caracteres")
        String name,

        @NotNull
        Long categoryId
) {
}
