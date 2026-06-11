package com.example.gerenciador.family.repository;

import com.example.gerenciador.family.Family;
import com.example.gerenciador.family.FamilyMember;
import com.example.gerenciador.family.FamilyRole;
import com.example.gerenciador.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    long countByUserAndRole(User user, FamilyRole role);

    Boolean existsByFamilyAndUser(Family family, User user) ;

    List<FamilyMember> findByUserId(Long userId);

    List<FamilyMember> findByFamilyId(Long familyId);

    Optional<FamilyMember> findByFamilyAndUser(Family family, User user);
}
