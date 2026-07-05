package com.example.gerenciador.jwt;

import com.example.gerenciador.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET =
            "sua-chave-super-secreta-com-pelo-menos-32-caracteres";

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // =========================
    // GERAR TOKEN
    // =========================
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getKey())
                .compact();
    }

    // =========================
    // EXTRAIR USERNAME
    // =========================
    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

    // =========================
    // EXTRAIR CLAIMS
    // =========================
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // =========================
    // EXPIRAÇÃO
    // =========================
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // =========================
    // VALIDAÇÃO COMPLETA (USE ISSO NO FILTER)
    // =========================
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUserName(token);

            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token);

        } catch (Exception e) {
            // token inválido, expirado ou corrompido
            return false;
        }
    }
}