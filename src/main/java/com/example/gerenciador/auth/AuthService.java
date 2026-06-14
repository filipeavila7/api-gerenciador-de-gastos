package com.example.gerenciador.auth;

import com.example.gerenciador.auth.dto.LoginRequest;
import com.example.gerenciador.auth.dto.LoginResponse;
import com.example.gerenciador.exceptions.UserNotFoundException;
import com.example.gerenciador.jwt.JwtService;
import com.example.gerenciador.user.entity.User;
import com.example.gerenciador.user.repository.UserRepository;
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

    public LoginResponse login(LoginRequest request){

        // autenticar o usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.passwprd()
                )
        );


        User user = repository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        // gerar token para o usuario logado
        String token = jwtService.generateToken(user);

        // retornar o token
        return new LoginResponse(token);
    }
}
