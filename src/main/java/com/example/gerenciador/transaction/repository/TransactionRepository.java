package com.example.gerenciador.transaction.repository;

import com.example.gerenciador.transaction.dto.TransactionBalanceMonthResponse;
import com.example.gerenciador.transaction.entity.Transaction;
import com.example.gerenciador.transaction.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    SELECT t
    FROM Transaction t
    WHERE t.family.id = :familyId
      AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:type IS NULL OR t.transactionType = :type)
      AND (:start IS NULL OR t.dateTime >= :start)
      AND (:end IS NULL OR t.dateTime < :end)
""")
    Page<Transaction> search(
            @Param("familyId") Long familyId,
            @Param("title") String title,
            @Param("type") TransactionType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
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




    @Query("""
    SELECT new com.example.gerenciador.transaction.dto.TransactionBalanceMonthResponse(

        COALESCE(
            SUM(
                CASE
                    WHEN t.transactionType = com.example.gerenciador.transaction.entity.TransactionType.INCOME
                    THEN t.amount
                    ELSE 0
                END
            ),
            0
        ),

        COALESCE(
            SUM(
                CASE
                    WHEN t.transactionType = com.example.gerenciador.transaction.entity.TransactionType.EXPENSE
                    THEN t.amount
                    ELSE 0
                END
            ),
            0
        )

    )
    FROM Transaction t
    WHERE t.family.id = :familyId
      AND t.dateTime >= :start
      AND t.dateTime < :end
""")
    TransactionBalanceMonthResponse getBalanceByMonth(
            @Param("familyId") Long familyId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
