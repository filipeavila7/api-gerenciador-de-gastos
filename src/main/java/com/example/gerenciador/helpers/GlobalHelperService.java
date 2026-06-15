package com.example.gerenciador.helpers;

import com.example.gerenciador.exceptions.AccessDeniedException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.family.entity.FamilyRole;
import com.example.gerenciador.family.repository.FamilyMemberRepository;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalHelperService {
    private final FamilyMemberRepository familyMemberRepository;

    public void getAdminMemberOrThrow(Family family, User user) {
        FamilyMember member = familyMemberRepository
                .findByFamilyAndUser(family, user)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        if (member.getRole() != FamilyRole.ADMIN) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
