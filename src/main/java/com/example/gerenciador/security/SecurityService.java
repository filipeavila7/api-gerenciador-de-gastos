package com.example.gerenciador.security;

import com.example.gerenciador.exceptions.AccessDeniedException;
import com.example.gerenciador.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public User getLoggedUser() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                !(auth.getPrincipal() instanceof User)) {

            throw new AccessDeniedException("Usuário não autenticado");
        }

        return (User) auth.getPrincipal();
    }
}
