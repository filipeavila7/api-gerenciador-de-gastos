package com.example.gerenciador.products.repository;

import com.example.gerenciador.products.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Products, Long> {

    Page<Products> findByFamilyId(Long familyId, Pageable pageable);

    Optional<Products> findByIdAndFamilyId(Long productId, Long familyId);

    List<Products> findAllByIdInAndFamilyId(List<Long> ids, Long familyId);
}
