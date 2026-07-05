package com.example.gerenciador.utils.uploads.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadService {
    private final String UPLOAD_DIR = "uploads";

    public String uploadImage(MultipartFile file){


        try {
            // criar a pasta de uploads caso ela não exista
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // gera nome único
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // resolve = “juntar caminho + nome do arquivo”
            Path filePath = uploadPath.resolve(fileName);

            // salva arquivo
            Files.copy(file.getInputStream(), filePath);

            // retorna URL
            return "/uploads/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da imagem");
        }
    }
}
