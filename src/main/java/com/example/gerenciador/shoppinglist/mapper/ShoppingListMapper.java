package com.example.gerenciador.shoppinglist.mapper;

import com.example.gerenciador.shoppinglist.dto.ShoppingListResponse;
import com.example.gerenciador.shoppinglist.entity.ShoppingList;
import org.springframework.stereotype.Component;

@Component
public class ShoppingListMapper {

    public ShoppingListResponse toShoppingListResponse(ShoppingList s){
        return new ShoppingListResponse(
                s.getId(),
                s.getFamily().getId(),
                s.getName(),
                s.getCreatedAt()
        );
    }
}
