package com.example.gerenciador.purchase.entity;


import com.example.gerenciador.products.entity.Products;
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
@Table(name = "purchase_itens") // itens dentro do bloco
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
    private Products products;


    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

}
