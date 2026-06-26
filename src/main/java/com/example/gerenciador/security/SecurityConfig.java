package com.example.gerenciador.security;


import com.example.gerenciador.jwt.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
    como o Spring vai autenticar usuários
    como vai proteger rotas
    como vai validar JWT
    como senha é criptografada
*/


@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    // configuração de hash de senha
    /// Define como senhas são criptografadas e comparadas.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // É o motor de autenticação do Spring Security.
    // verifica se a senha do usuario esta corretta
    // Spring monta tudo (UserDetailsService, PasswordEncoder etc.)
    // ó expõe como Bean pra usar no login
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    // define todas as regras de segurança
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // desliga proteção csrf pois ja esta usando jwt
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS // não existe login salvo no servidor, servidor não guarda sessão e cada request precisa de jwt
                        )
                )

                // definir quem acessa cada rota
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/auth/**"
                        ).permitAll()

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/users/**")
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )
                .userDetailsService(userDetailsService) // caso busque usuario, use userDetails
                .addFilterBefore( // antes do Spring tentar autenticar, roda meu filtro JWT
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}