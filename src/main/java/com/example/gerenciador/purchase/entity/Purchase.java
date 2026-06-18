package com.example.gerenciador.purchase.entity;


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
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "purchase") // bloco de commpras
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // relação com familia
    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    // relação com a tabela intermediário que junta o pacote de compra com produto
    @OneToMany(mappedBy = "purchase")
    private List<PurchaseItens> itens = new ArrayList<>();



    @Column(nullable = false)
    LocalDateTime dateTime;


}
