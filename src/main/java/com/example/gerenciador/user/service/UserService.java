package com.example.gerenciador.user.service;


import com.example.gerenciador.exceptions.AccessDeniedException;
import com.example.gerenciador.exceptions.EmailAlreadyExistsException;
import com.example.gerenciador.exceptions.UserNotFoundException;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.entity.UserRole;
import com.example.gerenciador.user.mapper.UserMapper;
import com.example.gerenciador.user.repository.UserRepository;
import com.example.gerenciador.user.dto.UserRequest;
import com.example.gerenciador.user.dto.UserResponse;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final UserMapper userMapper;


    // ================ GET ======================

    // retorna os dados usuario logado
    public UserResponse getMe(){
        User loggedUser = securityService.getLoggedUser();

        return userMapper.toUserResponse(loggedUser);
    }


    // ================ POST ======================

    public UserResponse createUser(UserRequest request){
        if (repository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(request.password()));

        repository.save(user);

        return userMapper.toUserResponse(user);
    }



    // ================ DELETE ======================

    // Próprio usuario apagar o perfil (Fszer verificação de id logado)
    public void deleteUserById(Long id){
        User loggedUser = securityService.getLoggedUser();

        if (!loggedUser.getId().equals(id)){
            throw new AccessDeniedException("Acess denied");
        }

       repository.delete(loggedUser);
    }


    // ================ PUT ======================

    // Próprio usuario logado se editar (Fazer verificação do id logado)
    public UserResponse editUserById(Long id, UserRequest request){
        User loggedUser = securityService.getLoggedUser();

        if (!loggedUser.getId().equals(id)){
            throw new AccessDeniedException("Acess denied");
        }

        User user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (request.name() != null) {
            user.setName(request.name());
        }

        if(request.email() != null &&
                !request.email().equals(user.getEmail())){

            repository.findByEmail(request.email())
                    .ifPresent(u -> {
                        throw new EmailAlreadyExistsException();
                    });

            user.setEmail(request.email());
        }

        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.profileImg() != null){
            user.setProfileImg(request.profileImg());
        }

        repository.save(user);

        return userMapper.toUserResponse(user);

    }

}
