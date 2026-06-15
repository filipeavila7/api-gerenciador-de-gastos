package com.example.gerenciador.category.mapper;

import com.example.gerenciador.category.dto.CategoryResponse;
import com.example.gerenciador.category.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toCategoryResponse(Category c){
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getFamily().getId()
        );
    }
}
