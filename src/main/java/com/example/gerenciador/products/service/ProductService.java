package com.example.gerenciador.products.service;

import com.example.gerenciador.category.entity.Category;
import com.example.gerenciador.category.repository.CategoryRepository;
import com.example.gerenciador.exceptions.AccessDeniedException;
import com.example.gerenciador.exceptions.CategoryNotFoundException;
import com.example.gerenciador.exceptions.FamilyNotFoundException;
import com.example.gerenciador.exceptions.ProductNotFoundExeption;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.family.repository.FamilyRepository;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.products.dto.ProductRequest;
import com.example.gerenciador.products.dto.ProductResponse;
import com.example.gerenciador.products.entity.Products;
import com.example.gerenciador.products.mapper.ProductMapper;
import com.example.gerenciador.products.repository.ProductRepository;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FamilyRepository familyRepository;
    private final SecurityService securityService;
    private final GlobalHelperService globalHelperService;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;


    public Page<ProductResponse> getMyProducts(Long familyId, Pageable pageable){
        User loggedUser = securityService.getLoggedUser();

        Family family = familyRepository.findById(familyId)
                .orElseThrow(FamilyNotFoundException::new);

        // so membros podem ver os produtos
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return productRepository.findByFamilyId(familyId, pageable)
                .map(productMapper::toProductResponse);

    }


    // usuario admin da familia criar produtos
    public ProductResponse createProduct(Long familyId, ProductRequest request){
        User loggedUser = securityService.getLoggedUser();

        // acha a familia
        Family family = familyRepository.findById(familyId)
                        .orElseThrow(FamilyNotFoundException::new);


        // verifica se o user é admin e pertence aquela familia
        FamilyMember member = globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // verificar se a categoria pertence a aquela familia do user que ta adcionando
        Category category = categoryRepository.findByIdAndFamilyId(request.categoryId(), familyId)
                .orElseThrow(CategoryNotFoundException::new);

        // cria o produto

        Products products = new Products();

        products.setName(request.name());
        products.setCategory(category);
        products.setFamily(family);

        productRepository.save(products);

        return productMapper.toProductResponse(products);
    }


    public void deleteProduct (Long familyId, Long productId){
        User loggedUser = securityService.getLoggedUser();

        // acha a familia
        Family family = familyRepository.findById(familyId)
                .orElseThrow(FamilyNotFoundException::new);

        // verifica se o user é admin e pertence aquela familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // acha o produto
        Products product = productRepository.findByIdAndFamilyId(productId, familyId)
                .orElseThrow(ProductNotFoundExeption::new);

        // apaga
        productRepository.delete(product);
    }
}
