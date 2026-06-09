package com.example.gerenciador.jwt;

import com.example.gerenciador.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET =
            "sua-chave-super-secreta-com-pelo-menos-32-caracteres";

    // transformar a chave secreta (SECRET) em um objeto Key que a biblioteca JJWT consegue usar para assinar e validar tokens.
    private Key getKey(){
        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    // gerar o token jwt
    public String generateToken (User user){

        return Jwts.builder()
                .subject(user.getEmail()) // definie o dono do token
                .claim("role", user.getRole().name()) // adciona a role do user ex: ADMIN, USER
                .issuedAt(new Date()) // data que o token foi criado
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // data de expiração (1 dia)
                .signWith(getKey()) // Assina o token usando a chave retornada por getKey(), Isso impede que alguém altere o conteúdo do JWT.
                .compact(); // transforma tudo em uma string JWT
    }

    // métodos usados quando o usuário já está autenticado e faz uma requisição para a API.
    public String extractUserName(String token){ // extrai o token e retorna o usuario dono dele
        return Jwts.parser()
                .verifyWith((SecretKey) getKey() )
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // verifica se o user dono do token, é o mesmo carregado do banco
    public boolean isValid(String token, UserDetails user){
        String userName = extractUserName(token);

        return  userName.equals(user.getUsername());
    }


}
