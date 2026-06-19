package com.example.gerenciador.purchase.repository;

import com.example.gerenciador.purchase.entity.PurchaseItens;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseItensRepository extends JpaRepository<PurchaseItens, Long> {

    boolean existsByProductIdAndPurchaseId(
            Long productId,
            Long purchaseId
    );

    boolean existsByPurchaseIdAndProductIdIn(
            Long purchaseId,
            List<Long> productIds
    );

    Page<PurchaseItens> findAllByPurchaseId(Long purchaseId, Pageable pageable);

    Optional<PurchaseItens> findByPurchaseIdAndProductId(Long purchaseId, Long productId);
}
