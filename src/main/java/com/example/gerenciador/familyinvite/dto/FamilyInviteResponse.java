package com.example.gerenciador.familyinvite.dto;

import java.time.LocalDateTime;

public record FamilyInviteResponse(
        String token,
        LocalDateTime expiresAt
) {
}
