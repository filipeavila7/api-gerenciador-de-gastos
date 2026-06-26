package com.example.gerenciador.shoppinglist.repository;

import com.example.gerenciador.shoppinglist.entity.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListItemRepository extends JpaRepository<ListItem, Long> {
    Optional<ListItem> findByIdAndShoppingListId(Long itemId, Long shoppingListId);
}
