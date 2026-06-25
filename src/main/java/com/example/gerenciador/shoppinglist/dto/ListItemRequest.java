package com.example.gerenciador.shoppinglist.dto;

import com.example.gerenciador.shoppinglist.entity.PriorityList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ListItemRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 25, message = "Nome deve ter entre 3 e 25 caracteres")
        String name,

        boolean done,  // opcional

        PriorityList priority // opcional, padrão NORMAL
) {
}
