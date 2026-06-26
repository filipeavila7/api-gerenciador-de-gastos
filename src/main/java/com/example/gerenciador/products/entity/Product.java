package com.example.gerenciador.products.entity;


import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.category.entity.Category;
import com.example.gerenciador.purchase.entity.PurchaseItens;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_products_family", columnList = "family_id"),
                @Index(name = "idx_products_category", columnList = "category_id"),
                @Index(name = "idx_products_family_active", columnList = "family_id, active")
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean active = true;


    // relação com categoria
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    // relação com a família
    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;


    @OneToMany(mappedBy = "product")
    List<PurchaseItens> purchaseItens = new ArrayList<>();
}
