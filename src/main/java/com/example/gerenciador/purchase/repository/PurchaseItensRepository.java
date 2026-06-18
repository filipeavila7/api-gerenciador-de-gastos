package com.example.gerenciador.purchase.repository;

import com.example.gerenciador.purchase.entity.PurchaseItens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseItensRepository extends JpaRepository<PurchaseItens, Long> {

    boolean existsByProductIdAndPurchaseId(
            Long productId,
            Long purchaseId
    );
}
