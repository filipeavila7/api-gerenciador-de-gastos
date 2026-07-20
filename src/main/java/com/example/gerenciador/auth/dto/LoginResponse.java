package com.example.gerenciador.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
