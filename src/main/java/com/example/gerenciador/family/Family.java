package com.example.gerenciador.family;


import com.example.gerenciador.category.Category;
import com.example.gerenciador.products.Products;
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

    // relação com a tabela intermediária que liga famílias com usuarios
    @OneToMany(mappedBy = "families")
    private List<FamilyMember> userMembres = new ArrayList<>();

    // relação inversa com produtos
    @OneToMany(mappedBy = "families")
    private List<Products> products = new ArrayList<>();

    // relação inversa com categorias
    @OneToMany(mappedBy = "families")
    private List<Category> categories = new ArrayList<>();


}
