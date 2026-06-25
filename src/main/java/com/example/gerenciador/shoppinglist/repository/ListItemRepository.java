package com.example.gerenciador.shoppinglist.repository;

import com.example.gerenciador.shoppinglist.entity.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListItemRepository extends JpaRepository<ListItem, Long> {
}
