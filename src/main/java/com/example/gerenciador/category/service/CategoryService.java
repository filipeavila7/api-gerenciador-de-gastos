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
import com.example.gerenciador.history.entity.HistoryAction;
import com.example.gerenciador.history.service.HistoryService;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SecurityService securityService;
    private final GlobalHelperService globalHelperService;
    private final CategoryMapper categoryMapper;
    private final HistoryService historyService;

    // ================ GET ======================

    // retornar as categprias daquela família, só membros da familia podem ver
    public Page<CategoryResponse> getMyCategorys(Long familyId, Pageable pageable){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario pertence a familia
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return categoryRepository.findByFamilyIdAndActiveTrue(familyId, pageable)
                .map(categoryMapper::toCategoryResponse);
    }


    // filtrar categorias por nome
    public Page<CategoryResponse> categorySearch(Long familyId, String name, Pageable pageable){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario pertence a familia
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return categoryRepository.findByFamilyIdAndNameContainingIgnoreCase(
                familyId, name, pageable)
                .map(categoryMapper::toCategoryResponse);
    }

    // ================ POST ======================

    // usauario admin criar categoria de produto
    @Transactional
    public CategoryResponse createCategory(Long familyId ,CategoryRequest request){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin da familia e pertence a ela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        Category category = new Category();

        category.setName(request.name());
        category.setFamily(family);
        category.setActive(true);

        categoryRepository.save(category);

        String message = "criou a categoria " + category.getName();
        historyService.createHistory(message, family, loggedUser, HistoryAction.CREATED_CATEGORY);

        return categoryMapper.toCategoryResponse(category);

    }



    // ================ PUT ======================

    // usuario admin editar categorias
    @Transactional
    public CategoryResponse updateCategory(Long familyId, Long categoryId, CategoryRequestUpdate request){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin da familia e pertence a ela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // pegar categoria, e ja garante que ela seja daquela familia
        Category category = categoryRepository.findByIdAndFamilyId(categoryId, familyId)
                .orElseThrow(CategoryNotFoundException::new);

        String name = category.getName();

        if (request.name() != null){
            category.setName(request.name());

            String message = "editou o nome da categoria " + name + " para " + category.getName();
            historyService.createHistory(message, family, loggedUser, HistoryAction.UPDATED_CATEGORY);
        }

        categoryRepository.save(category);



        return categoryMapper.toCategoryResponse(category);
    }


    // ================ DELETE ======================

    // usuario admin apagar uma categoria
    @Transactional
    public void deleteCategory(Long familyId, Long categoryId){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin da familia e pertence a ela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // pegar categoria e ja verifica se ela é daquela familia
        Category category = categoryRepository.findByIdAndFamilyId(categoryId, familyId)
                .orElseThrow(CategoryNotFoundException::new);

        category.setActive(false);

        String message = "deletou a categoria " + category.getName();
        historyService.createHistory(message, family, loggedUser, HistoryAction.DELETED_CATEGORY);

        categoryRepository.save(category);
    }


    // usuario admin apagar varias categorias
    @Transactional
    public void deleteCategories(Long familyId, CategoryDeleteRequest request){
        if(request.ids().size() != new HashSet<>(request.ids()).size()){
            throw new ConflictException(
                    "Existem IDs repetidos na requisição"
            );
        }

        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin da familia e pertence a ela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // pegar categorias
        List<Category> categories = categoryRepository.findAllByIdInAndFamilyId(request.ids(), familyId);


        if (categories.size() != request.ids().size()) {
            throw new CategoryNotFoundException();
        }

        categories.forEach(c ->{
                c.setActive(false);

                historyService.createHistory("deletou a categoria " + c.getName()
                        , family
                        , loggedUser
                        , HistoryAction.DELETED_CATEGORY);
    });

        categoryRepository.saveAll(categories);
    }



}
