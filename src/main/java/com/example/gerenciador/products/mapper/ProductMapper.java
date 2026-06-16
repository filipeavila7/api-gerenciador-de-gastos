package com.example.gerenciador.products.mapper;
import com.example.gerenciador.products.dto.ProductResponse;
import com.example.gerenciador.products.entity.Products;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toProductResponse(Products p){
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getCategory().getId(),
                p.getFamily().getId()
        );
    }
}
