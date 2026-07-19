package com.example.gerenciador.familyinvite.repository;

import com.example.gerenciador.familyinvite.entity.FamilyInvite;
import com.example.gerenciador.familyinvite.entity.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FamilyInviteRepository extends JpaRepository<FamilyInvite, Long> {

    Optional<FamilyInvite> findByToken(String token);

    boolean existsByToken(String token);

    List<FamilyInvite> findByStatusAndExpiresAtBefore(
            InviteStatus status,
            LocalDateTime date
    );


}