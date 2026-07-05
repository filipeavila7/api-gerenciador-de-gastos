package com.example.gerenciador.jwt;

import com.example.gerenciador.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// filtro qye vai ser executado em todas as requisições
// transformar um token JWT (string) em um usuário autenticado dentro do Spring, em todas as requisições protegidas.

/*
 Ele pega o JWT do header
 valida/decodifica ele
 carrega o usuário do banco
 e “injeta” esse usuário no Spring Security
 Se o token for válido, o Spring passa a tratar o usuário como logado
*/

@Component // Faz o Spring registrar esse filtro automaticamente.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // esse filtro roda uma vez por requisição HTTP

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // mwtodo executado automaticamente pelo Spring em cada request.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractUserName(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails user = userDetailsService.loadUserByUsername(email);

                // 🔥 valida o token antes de autenticar
                if (jwtService.isTokenValid(token, user)) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    SecurityContextHolder.clearContext();
                }
            }

        } catch (Exception e) {
            // 🔥 token expirado ou inválido
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
