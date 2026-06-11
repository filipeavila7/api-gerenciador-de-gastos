package com.example.gerenciador.family.dto;

import com.example.gerenciador.family.FamilyRole;

import java.time.LocalDateTime;

public record MemberResponse(
        String name,
        String profileImg,
        FamilyRole role,
        LocalDateTime joinedAt
) {
}
