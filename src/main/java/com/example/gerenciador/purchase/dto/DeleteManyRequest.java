package com.example.gerenciador.purchase.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteManyRequest(
        @NotEmpty List<Long> ids
) {
}
