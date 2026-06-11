package com.example.gerenciador.family.dto;


import java.time.LocalDateTime;

public record FamilyResponse(
        Long id,
        String name,
        LocalDateTime createdAt,
        String profileImg
) {
}
