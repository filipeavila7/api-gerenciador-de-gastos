package com.example.gerenciador.family.entity;


import com.example.gerenciador.category.entity.Category;
import com.example.gerenciador.history.entity.History;
import com.example.gerenciador.products.entity.Product;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.shoppinglist.entity.ShoppingList;
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
    private List<Product> products = new ArrayList<>();

    // relação inversa com categorias
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();

    // relação inversa com purchase
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<Purchase> purchases = new ArrayList<>();

    // relação inversa com lista de compras
    @OneToMany(mappedBy = "family",  cascade = CascadeType.ALL)
    private List<ShoppingList> shoppingLists = new ArrayList<>();

    // relação inversa com historico
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<History> histories =  new ArrayList<>();
}
