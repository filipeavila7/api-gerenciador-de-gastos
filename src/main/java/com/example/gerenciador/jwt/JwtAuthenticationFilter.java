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

        // pega o header autorization (Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...)
        String authHeader =
                request.getHeader("Authorization");

        // Se não tem token → deixa a requisição passar sem autenticar
        // Não quebra a API, Só não autentica o usuário
        // mportante porque nem todas as rotas precisam de login (ex: login, cadastro)
        if(authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // pegar o token e remover os 7 caracteres de bearer e fica so o token
        String token =
                authHeader.substring(7);

        // extrai o usuario do token
        String email =
                jwtService.extractUserName(token);

        // Verificar se já não está autenticado, evita se autenticar 2 vezes ou subscrever autenticação existente
        if(email != null &&
                SecurityContextHolder.getContext()
                        .getAuthentication() == null) {
            // busca o usuario no banco pelo email extraído no token
            UserDetails user =
                    userDetailsService
                            .loadUserByUsername(email);

            // cria o objeto de autenticação
            // ele cria o objeto que o Spring entende como usuario logado
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            user, // ususario
                            null, // senha não precisa aqui
                            user.getAuthorities() // role -> ADMIN, USER
                    );

            // joga no contexto do spring, agora ele sabe o user autenticado, quem ele é, e as permissões dele
            SecurityContextHolder.getContext()
                    .setAuthentication(auth);
        }

        // Libera a requisição para continuar o fluxo normal da API
        filterChain.doFilter(request, response);

    }
}
