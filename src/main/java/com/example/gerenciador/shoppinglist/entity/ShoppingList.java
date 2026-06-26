package com.example.gerenciador.shoppinglist.entity;

import com.example.gerenciador.family.entity.Family;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(
        name = "shopping_list",
        indexes = {
                @Index(name = "idx_shopping_list_family", columnList = "family_id")
        }
) // lista de compras
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    String name;

    // id da familia
    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;


    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL)
    List<ListItem> listItems = new ArrayList<>();


    @Column(nullable = false)
    private LocalDateTime createdAt;
}
