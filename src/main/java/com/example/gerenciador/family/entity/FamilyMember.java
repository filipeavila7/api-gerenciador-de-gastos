package com.example.gerenciador.family.entity;

import com.example.gerenciador.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// tabela intermediária entre family e user

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "family_member",
        indexes = {
                @Index(name = "idx_family_member_family", columnList = "family_id"),
                @Index(name = "idx_family_member_user", columnList = "user_id"),
                @Index(name = "idx_family_member_family_user", columnList = "family_id, user_id")
        }
)
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // fk de user e family:

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    private FamilyRole role;

}
