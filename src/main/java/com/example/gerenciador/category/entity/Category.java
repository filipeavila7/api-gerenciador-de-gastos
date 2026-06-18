package com.example.gerenciador.category.entity;


import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.products.entity.Product;
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
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // uma categoria pertence a varios produtos
    @OneToMany(mappedBy = "products")
    private List<Product> products = new ArrayList<>();

    // relação com a família
    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;
}
