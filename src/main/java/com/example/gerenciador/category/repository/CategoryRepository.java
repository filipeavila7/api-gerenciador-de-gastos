package com.example.gerenciador.category.repository;

import com.example.gerenciador.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
