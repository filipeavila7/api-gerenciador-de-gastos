package com.example.gerenciador.products.controller;

import com.example.gerenciador.products.service.AdminProductService;
import com.example.gerenciador.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final AdminProductService adminProductService;
    private final ProductService productService;

    // ================ ROTAS ADMIN ======================

    // ================ GET ======================


    // ================ POST ======================


    // ================ PUT ======================


    // ================ DELETE ======================




    // ================ ROTAS USER ======================

    // ================ GET ======================


    // ================ POST ======================


    // ================ PUT ======================


    // ================ DELETE ======================
}
