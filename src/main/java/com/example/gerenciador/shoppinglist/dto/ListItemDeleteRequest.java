package com.example.gerenciador.shoppinglist.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ListItemDeleteRequest(

        @NotEmpty
        List<Long> ids
) {
}
