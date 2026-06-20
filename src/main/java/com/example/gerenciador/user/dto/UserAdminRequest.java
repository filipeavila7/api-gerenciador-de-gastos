package com.example.gerenciador.user.dto;

import com.example.gerenciador.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserAdminRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 25, message = "Nome deve ter entre 3 e 25 caracteres")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 100, message = "Senha deve ter pelo menos 6 caracteres")
        String password,


        String profileImg,

        @NotNull
        UserRole role
) {
}
