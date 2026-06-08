package com.example.gerenciador.user;


import com.example.gerenciador.exceptions.EmailAlreadyExistsException;
import com.example.gerenciador.user.dto.UserRequest;
import com.example.gerenciador.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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




    }
}
