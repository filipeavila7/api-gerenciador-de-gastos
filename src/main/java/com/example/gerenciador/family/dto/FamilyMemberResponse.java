package com.example.gerenciador.family.dto;

import com.example.gerenciador.family.Family;
import com.example.gerenciador.family.FamilyRole;
import com.example.gerenciador.user.User;

import java.time.LocalDateTime;

public record FamilyMemberResponse(
        Long id,
        Family family,
        User userMember,
        LocalDateTime joinedAt,
        FamilyRole familyRole

) {
}
