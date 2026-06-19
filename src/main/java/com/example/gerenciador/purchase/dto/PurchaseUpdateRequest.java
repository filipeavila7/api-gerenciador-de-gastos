package com.example.gerenciador.purchase.dto;

import jakarta.validation.constraints.Size;

public record PurchaseUpdateRequest(

        @Size(min = 3, max = 25, message = "Nome deve ter entre 3 e 25 caracteres")
        String name
) {
}
