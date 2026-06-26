package com.example.gerenciador.transaction.entity;

import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.purchase.entity.Purchase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(
        name = "transaction",
        indexes = {
                @Index(name = "idx_transaction_family", columnList = "family_id"),
                @Index(name = "idx_transaction_purchase", columnList = "purchase_id"),
                @Index(name = "idx_transaction_family_datetime", columnList = "family_id, dateTime")
        }
)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;


    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String title;

    @Column
    private String description; // opcional


    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

}
