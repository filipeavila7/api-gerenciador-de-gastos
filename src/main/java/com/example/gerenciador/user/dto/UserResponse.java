package com.example.gerenciador.user.dto;

import com.example.gerenciador.user.UserRole;

public record UserResponse(
        String name,
        String email,
        String password,
        String profileImg,
        UserRole role
) {
}
