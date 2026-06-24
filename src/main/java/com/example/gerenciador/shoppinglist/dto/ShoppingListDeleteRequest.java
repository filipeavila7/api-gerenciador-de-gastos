package com.example.gerenciador.shoppinglist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ShoppingListDeleteRequest(

        @NotEmpty
        List<Long> ids
) {
}
