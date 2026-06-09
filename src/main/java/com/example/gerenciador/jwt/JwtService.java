package com.example.gerenciador.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Service
public class JwtService {
    private static final String SECRET =
            "sua-chave-super-secreta-com-pelo-menos-32-caracteres";


    private Key getKey(){
        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }


}
