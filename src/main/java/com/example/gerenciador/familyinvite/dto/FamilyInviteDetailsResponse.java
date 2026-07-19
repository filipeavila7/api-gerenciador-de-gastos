package com.example.gerenciador.familyinvite.dto;

import com.example.gerenciador.familyinvite.entity.InviteStatus;

import java.time.LocalDateTime;

public record FamilyInviteDetailsResponse(
        String familyName,
        String createdByName,
        LocalDateTime expiresAt,
        InviteStatus status
) {
}
