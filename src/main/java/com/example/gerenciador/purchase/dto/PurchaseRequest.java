package com.example.gerenciador.purchase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PurchaseRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 25, message = "Nome deve ter entre 3 e 25 caracteres")
        String name

) {
}
