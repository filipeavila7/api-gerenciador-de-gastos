package com.example.gerenciador.auth.service;

import com.example.gerenciador.auth.dto.LoginRequest;
import com.example.gerenciador.auth.dto.LoginResponse;
import com.example.gerenciador.exceptions.UserNotFoundException;
import com.example.gerenciador.jwt.JwtService;
import com.example.gerenciador.security.refresh.entity.RefreshToken;
import com.example.gerenciador.security.refresh.repository.RefreshTokenRepository;
import com.example.gerenciador.security.refresh.service.RefreshTokenService;
import com.example.gerenciador.user.entity.User;
import com.example.gerenciador.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;




    @Transactional
    public LoginResponse login(LoginRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );


        User user = repository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);


        String token = jwtService.generateToken(user);


        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.flush();






        RefreshToken refresh =
                refreshTokenService.create(user);


        return new LoginResponse(
                token,
                refresh.getToken()
        );
    }

    // refresh token
    public LoginResponse refresh(String refreshToken){

        // valida o refresh token
        RefreshToken token =
                refreshTokenService.validate(refreshToken);


        User user = token.getUser();


        // cria um novo jwt
        String newAccessToken =
                jwtService.generateToken(user);


        return new LoginResponse(
                newAccessToken,
                refreshToken
        );
    }
}
