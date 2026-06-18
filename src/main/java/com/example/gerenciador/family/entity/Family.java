package com.example.gerenciador.family.entity;


import com.example.gerenciador.category.entity.Category;
import com.example.gerenciador.products.entity.Products;
import com.example.gerenciador.purchase.Purchase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "families")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String profileImg;

    // cascadeType ALL -> caso a familia seja apagada, todas as registros onde estão o seu id são apagados tambem

    // relação com a tabela intermediária que liga famílias com usuarios
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<FamilyMember> userMembres = new ArrayList<>();

    // relação inversa com produtos
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<Products> products = new ArrayList<>();

    // relação inversa com categorias
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();

    // relação inversa com purchase
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<Purchase> purchases = new ArrayList<>();
}
