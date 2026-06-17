package com.example.gerenciador.products.controller;

import com.example.gerenciador.products.dto.ProductResponse;
import com.example.gerenciador.products.service.AdminProductService;
import com.example.gerenciador.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get")
    public ResponseEntity<Page<ProductResponse>> adminGetAllProducts(
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable){

        return ResponseEntity.ok(adminProductService.adminGetAllProducts(pageable));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get/{familyId}")
    public ResponseEntity<Page<ProductResponse>> adminGetProductsByfamilyId(
            @PathVariable Long familyId,
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable){

        return ResponseEntity.ok(adminProductService.adminGetProductsByfamilyId(familyId, pageable));
    }

    // ================ POST ======================


    // ================ PUT ======================


    // ================ DELETE ======================




    // ================ ROTAS USER ======================

    // ================ GET ======================


    // ================ POST ======================


    // ================ PUT ======================


    // ================ DELETE ======================
}
