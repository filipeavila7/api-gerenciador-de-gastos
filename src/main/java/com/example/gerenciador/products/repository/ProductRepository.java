package com.example.gerenciador.products.repository;

import com.example.gerenciador.products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByFamilyId(Long familyId, Pageable pageable);

    Page<Product> findByFamilyIdAndActiveTrue(
            Long familyId,
            Pageable pageable
    );

    @Query("""
    SELECT p
    FROM Product p
    WHERE p.category.family.id = :familyId
      AND p.active = true
      AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:categoryId IS NULL OR p.category.id = :categoryId)
""")
    Page<Product> search(
            @Param("familyId") Long familyId,
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );


    List<Product> findAllByIdInAndFamilyIdAndActiveTrue(
            List<Long> ids,
            Long familyId
    );

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
