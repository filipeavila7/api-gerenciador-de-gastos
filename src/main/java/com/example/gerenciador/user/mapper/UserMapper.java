package com.example.gerenciador.user.mapper;

import com.example.gerenciador.user.dto.UserAdminResponse;
import com.example.gerenciador.user.dto.UserResponse;
import com.example.gerenciador.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse (User u){
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getProfileImg()


        );
    }


    public UserAdminResponse toUserAdminResponse (User u){
        return new UserAdminResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getProfileImg(),
                u.getRole()

        );
    }
}
