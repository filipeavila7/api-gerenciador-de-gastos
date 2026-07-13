package com.example.gerenciador.purchase.repository;

import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.purchase.entity.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByIdAndFamilyId(Long purchaseId, Long familyId);

    Page<Purchase> findAllByFamilyId(Long familyId, Pageable pageable);

    List<Purchase> findAllByFamilyIdAndIdIn(Long familyId, List<Long> ids);

    @Query("""
    SELECT p
    FROM Purchase p
    WHERE p.family.id = :familyId
      AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:status IS NULL OR p.purchaseStatus = :status)
      AND (:start IS NULL OR p.dateTime >= :start)
      AND (:end IS NULL OR p.dateTime < :end)
""")
    Page<Purchase> search(
            @Param("familyId") Long familyId,
            @Param("name") String name,
            @Param("status") PurchaseStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}
