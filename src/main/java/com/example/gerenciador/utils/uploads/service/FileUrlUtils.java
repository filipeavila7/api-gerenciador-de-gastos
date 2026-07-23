package com.example.gerenciador.utils.uploads.service;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class FileUrlUtils {

    public static String toPublicUrl(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(path.startsWith("/") ? path : "/" + path)
                .toUriString();
    }
}