package com.example.gerenciador.category.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryDeleteRequest(

        @NotNull
        @NotEmpty
        List<Long> ids
) {
}
