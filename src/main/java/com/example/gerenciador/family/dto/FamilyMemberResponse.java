package com.example.gerenciador.family.dto;

import com.example.gerenciador.family.entity.FamilyRole;

import java.time.LocalDateTime;

public record FamilyMemberResponse(
        Long id,
        Long familyId,
        Long userId,
        String userName,
        String userProfileImg,
        LocalDateTime joinedAt,
        FamilyRole familyRole

) {
}
