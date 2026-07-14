package com.example.gerenciador.family.repository;

import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.family.entity.FamilyRole;
import com.example.gerenciador.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    long countByUserAndRole(User user, FamilyRole role);

    long countByFamilyId(Long familyId);

    Boolean existsByFamilyAndUser(Family family, User user) ;

    Page<FamilyMember> findByUserId(Long userId, Pageable pageable);

    List<FamilyMember> findByFamilyId(Long familyId);

    Optional<FamilyMember> findByFamilyAndUser(Family family, User user);

    Optional<FamilyMember> findByFamilyIdAndUserId(Long familyId, Long userId);
}
