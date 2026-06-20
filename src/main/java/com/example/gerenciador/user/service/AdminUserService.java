package com.example.gerenciador.user.service;


import com.example.gerenciador.exceptions.EmailAlreadyExistsException;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.user.dto.UpdateAdminUserRequest;
import com.example.gerenciador.user.dto.UserAdminRequest;
import com.example.gerenciador.user.dto.UserAdminResponse;
import com.example.gerenciador.user.entity.User;
import com.example.gerenciador.user.mapper.UserMapper;
import com.example.gerenciador.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GlobalHelperService globalHelperService;
    private final PasswordEncoder passwordEncoder;

    // ================ GET ======================

    // admin geral pode ver todos os usuarios logados
    public Page<UserAdminResponse> adminGetAllUsers(Pageable pageable){
        return userRepository.findAll(pageable)
                .map(userMapper::toUserAdminResponse);
    }

    public UserAdminResponse adminGetUser(Long id){
        User user = globalHelperService.getUserOrThrow(id);

        return userMapper.toUserAdminResponse(user);
    }


    // ================ POST ======================

    // usuario admin pode criar novos usuarios podendo escolher a role
    @Transactional
    public UserAdminResponse adminCreateUser(UserAdminRequest request){
        userRepository.findByEmail(request.email())
                .orElseThrow(EmailAlreadyExistsException::new);

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setProfileImg(request.profileImg());
        user.setRole(request.role());

        userRepository.save(user);

        return userMapper.toUserAdminResponse(user);
    }


    // ================ PUT ======================

    // admin geral pode editar qualquer usuario
    @Transactional
    public UserAdminResponse adminUpdateUserById(Long id, UpdateAdminUserRequest request){
        User user = globalHelperService.getUserOrThrow(id);

        if (request.name() != null){
            user.setName(request.name());
        }

        if(request.email() != null &&
                !request.email().equals(user.getEmail())){

            userRepository.findByEmail(request.email())
                    .ifPresent(u -> {
                        throw new EmailAlreadyExistsException();
                    });

            user.setEmail(request.email());
        }

        if (request.password() != null){
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.profileImg() != null){
            user.setProfileImg(request.profileImg());
        }

        if (request.role() != null){
            user.setRole(request.role());
        }

        userRepository.save(user);

        return userMapper.toUserAdminResponse(user);

    }

    // desativar conta de usuario
    @Transactional
    public void adminUserDisableAccount(Long id){
        User user = globalHelperService.getUserOrThrow(id);

        user.setEnabled(false);
        userRepository.save(user);
    }

    // ativar conta de usuario
    @Transactional
    public void adminUserEnableAccount(Long id){
        User user =globalHelperService.getUserOrThrow(id);

        user.setEnabled(true);
        userRepository.save(user);
    }

    // ================ DELETE ======================

    // admin geral pode apagar usuarios
    @Transactional
    public void adminDeleteUserById(Long id){
       User user = globalHelperService.getUserOrThrow(id);
       userRepository.delete(user);
    }

}
