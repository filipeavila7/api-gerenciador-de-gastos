package com.example.gerenciador.transaction.dto;

import com.example.gerenciador.transaction.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransactionIncomeRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 25, message = "Nome deve ter entre 3 e 25 caracteres")
        String title,

        @NotNull
        @Positive
        BigDecimal ammount,

        @Size(max = 120)
        String description // opcional



) {
}
