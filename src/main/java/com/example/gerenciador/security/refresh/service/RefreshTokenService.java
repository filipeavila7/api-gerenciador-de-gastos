package com.example.gerenciador.security.refresh.service;


import com.example.gerenciador.exceptions.InvalidTokenException;
import com.example.gerenciador.security.refresh.entity.RefreshToken;
import com.example.gerenciador.security.refresh.repository.RefreshTokenRepository;
import com.example.gerenciador.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {


    private final RefreshTokenRepository repository;


    private static final long REFRESH_TOKEN_DURATION_DAYS = 30;



    /*
        Cria um refresh token novo para o usuário
     */
    public RefreshToken create(User user){

        RefreshToken refreshToken = new RefreshToken();


        refreshToken.setToken(
                UUID.randomUUID().toString()
        );


        refreshToken.setUser(user);


        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plusDays(REFRESH_TOKEN_DURATION_DAYS)
        );


        return repository.save(refreshToken);
    }



    /*
        Busca e valida se o refresh token ainda é válido
     */
    public RefreshToken validate(String token){


        RefreshToken refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new InvalidTokenException(
                                        "Refresh token inválido"
                                )
                        );



        if(refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())){


            repository.delete(refreshToken);


            throw new InvalidTokenException(
                    "Refresh token expirado"
            );
        }


        return refreshToken;
    }



    /*
        Remove refresh token no logout
     */
    public void delete(String token){

        RefreshToken refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new InvalidTokenException(
                                        "Refresh token inválido"
                                )
                        );


        repository.delete(refreshToken);
    }



    /*
        Remove todos os refresh tokens de um usuário
        útil quando troca senha ou força logout global
     */
    @Transactional
    public void deleteAll(User user){
        System.out.println("dentro do delete");
        repository.deleteByUser(user);
    }
}