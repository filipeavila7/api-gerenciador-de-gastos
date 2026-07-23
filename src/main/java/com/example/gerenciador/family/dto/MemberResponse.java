package com.example.gerenciador.family.dto;

import com.example.gerenciador.family.entity.FamilyRole;

import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        Long userId,
        String name,
        String profileImg,
        FamilyRole role,
        LocalDateTime joinedAt
) {
}
