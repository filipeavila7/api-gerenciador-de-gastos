package com.example.gerenciador.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @Size(min = 3, max = 25, message = "Nome deve ter entre 3 e 25 caracteres")
        String name,

        @Size(max = 255)
        String email,

        @Size(min = 6, max = 100, message = "Senha deve ter pelo menos 6 caracteres")
        String password,
        String profileImg
) {
}
