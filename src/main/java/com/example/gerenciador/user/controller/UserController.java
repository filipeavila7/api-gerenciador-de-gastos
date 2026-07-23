package com.example.gerenciador.user.controller;

import com.example.gerenciador.user.dto.*;
import com.example.gerenciador.user.service.AdminUserService;
import com.example.gerenciador.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AdminUserService adminUserService;


    // ================ ROTAS ADMIN ======================


    // ================ GET ======================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get")
    public ResponseEntity<Page<UserAdminResponse>> adminGetAllUsers(
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable){
        return ResponseEntity.ok(adminUserService.adminGetAllUsers(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get/user/{userId}")
    public ResponseEntity<UserAdminResponse> adminGetAllUsers(@PathVariable Long userId){
        return ResponseEntity.ok(adminUserService.adminGetUser(userId));
    }

    // ================ POST ======================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/new")
    public ResponseEntity<UserAdminResponse> adminCreateUser (
            @RequestBody UserAdminRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminUserService.adminCreateUser(request));
    }


    // ================ PUT ======================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update/user/{userId}")
    public ResponseEntity<UserAdminResponse> adminCreateUser (
           @PathVariable Long userId ,@RequestBody UpdateAdminUserRequest request){
        return ResponseEntity.ok(adminUserService.adminUpdateUserById(userId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/disable/user/{userId}")
    public ResponseEntity<Void> adminDisableAccount (@PathVariable Long userId){
        adminUserService.adminUserDisableAccount(userId);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/enable/user/{userId}")
    public ResponseEntity<Void> adminEnableAccount (@PathVariable Long userId){
        adminUserService.adminUserEnableAccount(userId);
        return ResponseEntity.noContent().build();
    }

    // ================ DELETE ======================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/user/{userId}")
    public ResponseEntity<Void> adminDeleteUserById(@PathVariable Long userId){
        adminUserService.adminDeleteUserById(userId);

        return ResponseEntity.noContent().build();
    }


    // ================ ROTAS USER ======================


    // ================ GET ======================

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(){
        return ResponseEntity.ok(userService.getMe());
    }


    // ================ POST ======================

    @PostMapping("/new")
    public ResponseEntity<UserResponse> crateUser(@Valid @RequestBody UserRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    // ================ PUT ======================

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateMe(@RequestBody UpdateUserRequest request){
        return ResponseEntity.ok(userService.editMe(request));
    }


    // ================ DELETE ======================

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMe(){
        userService.deleteMe();
        return ResponseEntity.noContent().build();

    }

}
