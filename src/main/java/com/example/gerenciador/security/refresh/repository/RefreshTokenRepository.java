package com.example.gerenciador.security.refresh.repository;


import com.example.gerenciador.security.refresh.entity.RefreshToken;
import com.example.gerenciador.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {


    Optional<RefreshToken> findByToken(String token);


    void deleteByUser(User user);


    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.user.id = :userId")
    void deleteByUserId(Long userId);
}