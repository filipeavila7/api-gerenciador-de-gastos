package com.example.gerenciador.transaction.repository;

import com.example.gerenciador.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByFamilyId(
            Long familyId,
            Pageable pageable
    );
}
