package com.example.gerenciador.purchase.entity;


import com.example.gerenciador.products.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


// tabela intermediária entre purchase e products
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(
        name = "purchase_items",
        indexes = {
                @Index(name = "idx_purchase_items_purchase", columnList = "purchase_id"),
                @Index(name = "idx_purchase_items_product", columnList = "product_id"),
                @Index(name = "idx_purchase_items_purchase_product", columnList = "purchase_id, product_id")
        }
) // itens dentro do bloco
public class PurchaseItens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relações:

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

}
