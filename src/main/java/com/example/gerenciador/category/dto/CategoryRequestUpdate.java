package com.example.gerenciador.category.dto;

import jakarta.validation.constraints.Size;

public record CategoryRequestUpdate(

        @Size(min = 3, max = 25, message = "Nome deve ter entre 3 e 25 caracteres")
        String name
) {
}
