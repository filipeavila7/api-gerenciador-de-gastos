package com.example.gerenciador.category.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 5, max = 15, message = "Nome deve ter entre 5 e 15 caracteres")
        String name
) {
}
