package com.example.gerenciador.user;


import com.example.gerenciador.exceptions.EmailAlreadyExistsException;
import com.example.gerenciador.exceptions.UserNotFoundException;
import com.example.gerenciador.user.dto.UserRequest;
import com.example.gerenciador.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest request){
        if (repository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        repository.save(user);

        return toResponse(user);
    }

    public List<UserResponse> getAllUsers(){
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // Próprio usuario apagar o perfil (Fszer verificação de id logado)
    public void deleteUserById(Long id){
       User userFind = repository.findById(id)
               .orElseThrow(UserNotFoundException::new);

       repository.delete(userFind);
    }


    // Próprio usuario logado se editar (Fazer verificação do id logado)
    public UserResponse editUserById(Long id, UserRequest request){
        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (request.name() != null) {
            user.setName(request.name());
        }

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.profileImg() != null){
            user.setProfileImg(request.profileImg());
        }

        repository.save(user);

        return toResponse(user);

    }


    private UserResponse toResponse (User u){
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getProfileImg(),
                u.getRole()

        );
    }
}
