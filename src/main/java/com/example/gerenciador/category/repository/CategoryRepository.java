package com.example.gerenciador.category.repository;

import com.example.gerenciador.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByFamilyId(Long familyId);
    Page<Category> findByFamilyId(Long familyId, Pageable pageable);
    Page<Category> findAll(Pageable pageable);
    Optional<Category> findByIdAndFamilyId(Long categoryId, Long familyId);
}
