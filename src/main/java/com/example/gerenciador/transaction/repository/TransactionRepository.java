package com.example.gerenciador.transaction.repository;

import com.example.gerenciador.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByFamilyId(
            Long familyId,
            Pageable pageable
    );

    long countByFamilyId(Long familyId);

    Optional<Transaction> findByFamilyIdAndId(
            Long familyId,
            Long transactionId
    );

    @EntityGraph(attributePaths = {
            "purchase",
            "purchase.itens",
            "purchase.itens.product",
            "purchase.itens.product.category"
    })
    Optional<Transaction> findWithDetailsByFamilyIdAndId(
            Long familyId,
            Long transactionId
    );


    @Query("""
    SELECT COALESCE(
        SUM(
            CASE
                WHEN t.transactionType = 'INCOME' THEN t.amount
                ELSE -t.amount
            END
        ), 0
    )
    FROM Transaction t
    WHERE t.family.id = :familyId
""")
    BigDecimal calculateBalance(Long familyId);
}
