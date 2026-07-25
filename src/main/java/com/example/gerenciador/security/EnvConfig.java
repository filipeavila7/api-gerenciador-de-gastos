package com.example.gerenciador.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvConfig {

    @Value("${DB_URL}")
    private String dbUrl;

    @Value("${DB_USER}")
    private String dbUser;

    @Value("${DB_PASSWORD}")
    private String dbPassword;


    @PostConstruct
    public void check() {
        log.info("DB_URL: {}", dbUrl);
        log.info("DB_USER: {}", dbUser);
        log.info("DB_PASSWORD: {}", dbPassword);
    }
}