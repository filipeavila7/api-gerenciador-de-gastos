package com.example.gerenciador.shoppinglist.repository;

import com.example.gerenciador.shoppinglist.entity.ShoppingList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    Page<ShoppingList> findAllByFamilyId(Long familyId, Pageable pageable);

    Optional<ShoppingList> findByFamilyIdAndId(Long familyId, Long shoppingListId);
}
