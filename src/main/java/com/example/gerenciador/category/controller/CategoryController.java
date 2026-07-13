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
    @GetMapping("/admin/get/family/{familyId}")
    public ResponseEntity<Page<CategoryResponse>> adminGetCategoriesByfamilyId(@PathVariable Long familyId,
        @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC ) Pageable pageable) {

        return ResponseEntity.ok(adminCategoryService.adminGetCategoriesByfamilyId(familyId, pageable));
    }


    // ================ POST ======================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/new/family/{familyId}")
    public ResponseEntity<CategoryResponse>  adminCreateCategories(@PathVariable Long familyId
            ,@Valid @RequestBody CategoryRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminCategoryService.adminCreateCategories(familyId, request));
    }

    // ================ PUT ======================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update/category/{categoryId}")
    public ResponseEntity<CategoryResponse>  adminCreateCategories(@PathVariable Long categoryId
            ,@Valid @RequestBody CategoryRequestUpdate request){
        return ResponseEntity.ok(adminCategoryService.adminUpdateCategory(categoryId, request));
    }

    // ================ DELETE ======================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/category/{categoryId}")
    public ResponseEntity<Void> adminDeleteCategoryById(@PathVariable Long categoryId){
        adminCategoryService.adminDeleteCategoryById(categoryId);

        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete")
    public ResponseEntity<Void> adminDeleteCategoriesByIds(@Valid @RequestBody CategoryDeleteRequest request){
        adminCategoryService.adminDeleteCategoriesByIds(request);

        return ResponseEntity.noContent().build();
    }



    // ================ ROTAS USER ======================

    // ================ GET ======================

    @GetMapping("/my/family/{familyId}")
    public ResponseEntity<Page<CategoryResponse>> getMyCategories(@PathVariable Long familyId
            , @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC ) Pageable pageable){

        return ResponseEntity.ok(categoryService.getMyCategorys(familyId, pageable));
    }


    @GetMapping("/my/family/{familyId}/search")
    public ResponseEntity<Page<CategoryResponse>> categorySearch(
            @PathVariable Long familyId,
            @RequestParam String name,
            @PageableDefault(size = 12, sort = "name", direction = Sort.Direction.ASC )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                categoryService.categorySearch(familyId, name, pageable)
        );
    }


    // ================ POST ======================

    @PostMapping("/new/family/{familyId}")
    public ResponseEntity<CategoryResponse> createCategory(@PathVariable Long familyId, @Valid @RequestBody CategoryRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(familyId, request));
    }

    // ================ PUT ======================

    @PutMapping("/update/family/{familyId}/category/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long familyId,
            @PathVariable Long categoryId, @Valid @RequestBody CategoryRequestUpdate requestUpdate){

        return ResponseEntity.ok(categoryService.updateCategory(familyId, categoryId, requestUpdate));
    }

    // ================ DELETE ======================

    @DeleteMapping("/delete/family/{familyId}/category/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long familyId, @PathVariable Long categoryId){
        categoryService.deleteCategory(familyId, categoryId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/delete/family/{familyId}")
    public ResponseEntity<Void> deleteCategories(@PathVariable Long familyId, @Valid @RequestBody CategoryDeleteRequest request){
        categoryService.deleteCategories(familyId, request);
        return ResponseEntity.noContent().build();
    }
}
