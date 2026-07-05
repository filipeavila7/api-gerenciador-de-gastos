package com.example.gerenciador.utils.uploads.controller;

import com.example.gerenciador.utils.uploads.service.UploadService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@AllArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping
    public ResponseEntity<String> upload(
            @RequestParam MultipartFile image
    ) {

        String imageUrl = uploadService.uploadImage(image);

        return ResponseEntity.ok(imageUrl);
    }
}
