package com.example.gerenciador.shoppinglist.dto;

import com.example.gerenciador.shoppinglist.entity.PriorityList;

public record LIstItemResponse(
        Long id,
        Long shoppingListId,
        Long familyId,
        String name,
        boolean done,
        PriorityList priority
) {
}
