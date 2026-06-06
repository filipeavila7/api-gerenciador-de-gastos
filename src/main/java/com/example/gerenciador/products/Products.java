package com.example.gerenciador.products;


import com.example.gerenciador.Family.Family;
import com.example.gerenciador.category.Category;
import com.example.gerenciador.purchase.PurchaseItens;
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
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;


    // relação com categoria
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    // relação com a família
    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;


    @OneToMany(mappedBy = "products")
    List<PurchaseItens> purchases = new ArrayList<>();
}
