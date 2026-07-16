package com.example.gerenciador.shoppinglist.mapper;

import com.example.gerenciador.shoppinglist.dto.LIstItemResponse;
import com.example.gerenciador.shoppinglist.dto.ShoppingListResponse;
import com.example.gerenciador.shoppinglist.entity.ListItem;
import com.example.gerenciador.shoppinglist.entity.ShoppingList;
import org.springframework.stereotype.Component;

@Component
public class ShoppingListMapper {

    public ShoppingListResponse toShoppingListResponse(ShoppingList s){
        return new ShoppingListResponse(
                s.getId(),
                s.getFamily().getId(),
                s.getName(),
                s.getCreatedAt(),
                s.getListItems().size()
        );
    }

    public LIstItemResponse toLIstItemResponse(ListItem l){
        return new LIstItemResponse(
                l.getId(),
                l.getShoppingList().getId(),
                l.getShoppingList().getFamily().getId(),
                l.getName(),
                l.getDone(),
                l.getPriorityList()
        );
    }
}
