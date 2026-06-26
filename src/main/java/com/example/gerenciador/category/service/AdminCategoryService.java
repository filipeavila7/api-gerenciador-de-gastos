package com.example.gerenciador.category.service;

import com.example.gerenciador.category.dto.CategoryDeleteRequest;
import com.example.gerenciador.category.dto.CategoryRequest;
import com.example.gerenciador.category.dto.CategoryRequestUpdate;
import com.example.gerenciador.category.dto.CategoryResponse;
import com.example.gerenciador.category.entity.Category;
import com.example.gerenciador.category.mapper.CategoryMapper;
import com.example.gerenciador.category.repository.CategoryRepository;
import com.example.gerenciador.exceptions.CategoryNotFoundException;
import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final GlobalHelperService globalHelperService;


    // ================ GET ======================

    // admin geral pode ver todas as categorias
    public Page<CategoryResponse> adminGetAllCategories(Pageable pageable){
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toCategoryResponse);
    }

    // admin geral pode ver todas as categorias de uma familia pelo id dela
    public Page<CategoryResponse> adminGetCategoriesByfamilyId(Long familyId, Pageable pageable){
        return categoryRepository.findAllByFamilyId(pageable, familyId)
                .map(categoryMapper::toCategoryResponse);
    }

    // ================ POST ======================

    // admin geral pode criar categorias para familias mesmo não sendo o membro admin dela
    public CategoryResponse adminCreateCategories(Long familyId, CategoryRequest request){

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        Category category = new Category();

        category.setName(request.name());
        category.setFamily(family);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    // ================ DELETE ======================

    // admin geral pode deletar uma categoria pelo id
    public void adminDeleteCategoryById(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        // verifica se tem algum produto com essa categoria
        if (productRepository.existsByCategoryId(categoryId)){
            throw new ConflictException("Essa categoria esta relacionada a algum produto");
        }

        categoryRepository.delete(category);
    }


    // admin geral pode deletar varias categorias pelos ids
    public void adminDeleteCategoriesByIds(CategoryDeleteRequest delete){

        if(delete.ids().size() != new HashSet<>(delete.ids()).size()){
            throw new ConflictException(
                    "Existem IDs repetidos na requisição"
            );
        }
        List<Category> categories = categoryRepository.findAllById(delete.ids());

        if (categories.size() != delete.ids().size()){
            throw new CategoryNotFoundException();
        }

        if (productRepository.existsByCategoryIdIn(delete.ids())){
        throw new ConflictException("Uma ou mais categorias estão relacionadas a produtos");
        }

        categoryRepository.deleteAll(categories);
    }

    // ================ PUT ======================

    // admin geral pode editar uma categoria pelo id dela
    public CategoryResponse adminUpdateCategory(Long categoryId, CategoryRequestUpdate request){

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if (request.name() != null){
            category.setName(request.name());
        }

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));

    }
}
