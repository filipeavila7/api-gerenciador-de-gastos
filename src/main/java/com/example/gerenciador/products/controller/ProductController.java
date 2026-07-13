package com.example.gerenciador.products.controller;

import com.example.gerenciador.products.dto.ProductDeleteRequest;
import com.example.gerenciador.products.dto.ProductRequest;
import com.example.gerenciador.products.dto.ProductResponse;
import com.example.gerenciador.products.dto.ProductUpdateRequest;
import com.example.gerenciador.products.service.AdminProductService;
import com.example.gerenciador.products.service.ProductService;
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
    @GetMapping("/admin/get/family/{familyId}")
    public ResponseEntity<Page<ProductResponse>> adminGetProductsByFamilyId(
            @PathVariable Long familyId,
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable){

        return ResponseEntity.ok(adminProductService.adminGetProductsByfamilyId(familyId, pageable));
    }

    // ================ POST ======================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/new/family/{familyId}")
    public ResponseEntity<ProductResponse> adminCreateProduct(
            @PathVariable long familyId, @Valid @RequestBody ProductRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminProductService.adminCreateProduct(familyId, request));
    }


    // ================ PUT ======================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update/family/{familyId}/product/{productId}")
    public ResponseEntity<ProductResponse> adminUpdateProduct(
            @PathVariable Long familyId, @PathVariable Long productId,
           @Valid @RequestBody ProductUpdateRequest request){
        return ResponseEntity.ok(adminProductService.adminUpdateProduct(familyId, productId, request));
    }


    // ================ DELETE ======================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/product/{productId}")
    public ResponseEntity<Void> adminDeleteProduct(@PathVariable Long productId){
        adminProductService.adminDeleteProduct(productId);

        return ResponseEntity.noContent().build();

    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete")
    public ResponseEntity<Void> adminDeleteProducts(@RequestBody ProductDeleteRequest request){
        adminProductService.adminDeleteProducts(request);

        return ResponseEntity.noContent().build();

    }


    // ================ ROTAS USER ======================

    // ================ GET ======================

    @GetMapping("/my/family/{familyId}")
    public ResponseEntity<Page<ProductResponse>> getMyProducts (@PathVariable Long familyId,
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable){
        return ResponseEntity.ok(productService.getMyProducts(familyId, pageable));

    }

    @GetMapping("/my/family/{familyId}/search")
    public ResponseEntity<Page<ProductResponse>> productSearch(
            @PathVariable Long familyId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(productService.productSearch(familyId, name, categoryId, pageable));
    }


    // ================ POST ======================

    @PostMapping("/new/family/{familyId}")
    public ResponseEntity<ProductResponse> createProduct (@PathVariable Long familyId,
            @Valid @RequestBody ProductRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(familyId, request));
    }

    // ================ PUT ======================

    @PutMapping("/update/family/{familyId}/product/{productId}")
    ResponseEntity<ProductResponse> updateProduct(@PathVariable Long familyId,
    @PathVariable Long productId, @Valid @RequestBody ProductUpdateRequest request){
        return ResponseEntity.ok(productService.updateProduct(familyId, productId, request));
    }

    // ================ DELETE ======================

    @DeleteMapping("/delete/family/{familyId}/product/{productId}")
    public ResponseEntity<Void> deleteProduct (@PathVariable Long familyId,
    @PathVariable Long productId ){
        productService.deleteProduct(familyId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/family/{familyId}/products")
    public ResponseEntity<Void> deleteProduct (@PathVariable Long familyId
    , @RequestBody ProductDeleteRequest request){
        productService.deleteProducts(familyId, request);
        return ResponseEntity.noContent().build();
    }


}
