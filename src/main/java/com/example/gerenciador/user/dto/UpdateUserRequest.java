package com.example.gerenciador.user.dto;

public record UpdateUserRequest(
        String name,
        String email,
        String password,
        String profileImg
) {
}
