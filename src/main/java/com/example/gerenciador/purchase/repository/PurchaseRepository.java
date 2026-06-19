package com.example.gerenciador.purchase.repository;

import com.example.gerenciador.purchase.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByIdAndFamilyId(Long purchaseId, Long familyId);

    Page<Purchase> findAllByFamilyId(Long familyId, Pageable pageable);

    List<Purchase> findAllByFamilyIdAndIdIn(Long familyId, List<Long> ids);


}
