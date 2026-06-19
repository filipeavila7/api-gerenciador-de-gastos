package com.example.gerenciador.products.service;


import com.example.gerenciador.category.entity.Category;
import com.example.gerenciador.category.repository.CategoryRepository;
import com.example.gerenciador.exceptions.CategoryNotFoundException;
import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.exceptions.ProductNotFoundExeption;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.products.dto.ProductDeleteRequest;
import com.example.gerenciador.products.dto.ProductRequest;
import com.example.gerenciador.products.dto.ProductResponse;
import com.example.gerenciador.products.dto.ProductUpdateRequest;
import com.example.gerenciador.products.entity.Product;
import com.example.gerenciador.products.mapper.ProductMapper;
import com.example.gerenciador.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductService {
    private final GlobalHelperService globalHelperService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;


    // ================ GET ======================

    // admin geral pode ver todos os produtos
    public Page<ProductResponse> adminGetAllProducts (Pageable pageable){
        return productRepository.findAll(pageable)
                .map(productMapper::toProductResponse);
    }

    // admin geral pode ver todos os produtos de uma familia pelo id dela
    public Page<ProductResponse> adminGetProductsByfamilyId(Long familyId, Pageable pageable){
        globalHelperService.getFamilyOrThrow(familyId);

        return productRepository.findByFamilyId(familyId, pageable)
                .map(productMapper::toProductResponse);
    }

    // ================ POST ======================

    // admin geral pode criar produtos para uma familia
    @Transactional
    public ProductResponse adminCreateProduct (Long familyId, ProductRequest request){
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // garante que a categoria é daquela familia
        Category category = categoryRepository.findByIdAndFamilyId(request.categoryId(), familyId)
                .orElseThrow(CategoryNotFoundException::new);

        // cria o produto
        Product products = new Product();

        products.setName(request.name());
        products.setCategory(category);
        products.setFamily(family);

        return productMapper.toProductResponse(productRepository.save(products));

    }


    // ================ PUT ======================

    // admin geral pode editar produtos de uma familia
    @Transactional
    public ProductResponse adminUpdateProduct(Long familyId, Long productId, ProductUpdateRequest request){
        // encontrar a familia
        globalHelperService.getFamilyOrThrow(familyId);


        Product product = productRepository.findByIdAndFamilyId(productId, familyId)
                .orElseThrow(ProductNotFoundExeption::new);

        if (request.name() != null){
            product.setName(request.name());
        }

        // verificar se a categoria pertence a aquela familia
        if (request.categoryId() != null){
            Category category = categoryRepository.findByIdAndFamilyId(request.categoryId(), familyId)
                    .orElseThrow(CategoryNotFoundException::new);

            product.setCategory(category);
        }

        return productMapper.toProductResponse(productRepository.save(product));
    }

    // ================ DELETE ======================

    // admin geral pode deletar produtos
    @Transactional
    public void adminDeleteProduct (Long productId){

        // acha o produto
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundExeption::new);

        // apaga
        productRepository.delete(product);
    }


    // admin geral pode deletar varios produtos
    @Transactional
    public void adminDeleteProducts (ProductDeleteRequest request){

        if(request.ids().size() != new HashSet<>(request.ids()).size()){
            throw new ConflictException(
                    "Existem IDs repetidos na requisição"
            );
        }

        List<Product> products = productRepository.findAllById(request.ids());

        if (products.size() != request.ids().size()){
            throw new ProductNotFoundExeption();
        }

        productRepository.deleteAll(products);
    }
}
