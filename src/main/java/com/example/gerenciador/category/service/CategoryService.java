package com.example.gerenciador.category.service;


import com.example.gerenciador.category.dto.CategoryRequest;
import com.example.gerenciador.category.dto.CategoryResponse;
import com.example.gerenciador.category.repository.CategoryRepository;
import com.example.gerenciador.family.repository.FamilyRepository;
import com.example.gerenciador.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SecurityService securityService;
    private final FamilyRepository familyRepository;


    public CategoryResponse createCategory(Long familyId ,CategoryRequest request){

    }
}
