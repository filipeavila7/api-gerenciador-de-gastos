package com.example.gerenciador.user;

import com.example.gerenciador.family.entity.FamilyMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String profileImg;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    // relação com a tabela intermediária que liga famílias com usuarios
    @OneToMany(mappedBy = "user")
    private List<FamilyMember> memberships = new ArrayList<>();


    // informar ao Spring quais permissões (authorities/roles) o usuário possui.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == UserRole.ADMIN){
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }

        return List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    // identificador do usuário no login -> email
    @Override
    public String getUsername() {
        return email;
    }

    // Define se a conta está expirada (não pode logar)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Define se a conta está bloqueada
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Define se a senha ainda é válida
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Define se a conta está ativa
    @Override
    public boolean isEnabled() {
        return true;
    }

}
