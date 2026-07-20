package com.example.gerenciador.auth.controller;

import com.example.gerenciador.auth.service.AuthService;
import com.example.gerenciador.auth.dto.LoginRequest;
import com.example.gerenciador.auth.dto.LoginResponse;
import com.example.gerenciador.security.refresh.entity.RefreshToken;
import com.example.gerenciador.security.refresh.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
          @Valid @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue("refreshToken") String token
    ){

        return ResponseEntity.ok(
                authService.refresh(token)
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue("refreshToken") String token
    ){

        refreshTokenService.delete(token);

        return ResponseEntity.ok().build();
    }
}
