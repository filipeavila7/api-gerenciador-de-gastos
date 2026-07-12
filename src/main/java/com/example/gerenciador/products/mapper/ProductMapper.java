package com.example.gerenciador.products.mapper;
import com.example.gerenciador.products.dto.ProductResponse;
import com.example.gerenciador.products.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponse toProductResponse(Product p){
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getFamily().getId()
        );
    }
}
