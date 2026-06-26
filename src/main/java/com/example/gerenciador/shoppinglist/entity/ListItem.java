package com.example.gerenciador.shoppinglist.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(
        name = "list_item",
        indexes = {
                @Index(name = "idx_list_item_shopping_list", columnList = "shopping_list_id")
        }
) // produtos dentro da lista
public class ListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shopping_list_id")
    private ShoppingList shoppingList;

    @Column(nullable = false)
    private String name;

    private Boolean done =  false;

    @Enumerated(EnumType.STRING)
    private PriorityList priorityList;
}
