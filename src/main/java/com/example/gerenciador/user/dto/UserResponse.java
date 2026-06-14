package com.example.gerenciador.user.dto;

import com.example.gerenciador.user.entity.UserRole;

public record UserResponse(
        Long id,
        String name,
        String email,
        String profileImg,
        UserRole role
) {
}
