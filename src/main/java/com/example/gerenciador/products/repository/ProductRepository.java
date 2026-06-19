package com.example.gerenciador.products.repository;

import com.example.gerenciador.products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByFamilyId(Long familyId, Pageable pageable);

    boolean existsByCategoryIdAndFamilyId(
            Long categoryId,
            Long familyId
    );

    boolean existsByCategoryIdInAndFamilyId(
            List<Long> categoryIds,
            Long familyId
    );



    boolean existsByCategoryId(Long categoryId);

    boolean existsByCategoryIdIn(List<Long> categoryIds);

    Optional<Product> findByIdAndFamilyId(Long productId, Long familyId);

    List<Product> findAllByIdInAndFamilyId(List<Long> ids, Long familyId);
}
