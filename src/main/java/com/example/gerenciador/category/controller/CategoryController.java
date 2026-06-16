package com.example.gerenciador.category.controller;


import com.example.gerenciador.category.dto.CategoryDeleteRequest;
import com.example.gerenciador.category.dto.CategoryRequest;
import com.example.gerenciador.category.dto.CategoryRequestUpdate;
import com.example.gerenciador.category.dto.CategoryResponse;
import com.example.gerenciador.category.service.AdminCategoryService;
import com.example.gerenciador.category.service.CategoryService;
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
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final AdminCategoryService adminCategoryService;


    // ================ ROTAS ADMIN ======================

    // ================ GET ======================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get")
    public ResponseEntity<Page<CategoryResponse>> adminGetAllCategories(@PageableDefault(
            size = 12, // 12 categorias por pagina
            sort = "name", // sortear pelo nome
            direction = Sort.Direction.ASC // ordem crescente
    ) Pageable pageable){

        return ResponseEntity.ok(adminCategoryService.adminGetAllCategories(pageable));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get/{familyId}")
    public ResponseEntity<Page<CategoryResponse>> adminGetCategoriesByfamilyId(@PathVariable Long familyId,
        @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC ) Pageable pageable) {

        return ResponseEntity.ok(adminCategoryService.adminGetCategoriesByfamilyId(familyId, pageable));
    }


    // ================ POST ======================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/add/{familyId}")
    public ResponseEntity<CategoryResponse>  adminCreateCategories(@PathVariable Long familyId
            ,@Valid @RequestBody CategoryRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminCategoryService.adminCreateCategories(familyId, request));
    }

    // ================ PUT ======================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/add/{categoryId}")
    public ResponseEntity<CategoryResponse>  adminCreateCategories(@PathVariable Long categoryId
            ,@RequestBody CategoryRequestUpdate request){
        return ResponseEntity.ok(adminCategoryService.adminUpdateCategory(categoryId, request));
    }

    // ================ DELETE ======================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/{categoryId}")
    public ResponseEntity<Void> adminDeleteCategoryById(@PathVariable Long categoryId){
        adminCategoryService.adminDeleteCategoryById(categoryId);

        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/{categoryId}")
    public ResponseEntity<Void> adminDeleteCategoriesByIds(@Valid @RequestBody CategoryDeleteRequest request){
        adminCategoryService.adminDeleteCategoriesByIds(request);

        return ResponseEntity.noContent().build();
    }

}
