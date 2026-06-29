package com.example.gerenciador.products.service;

import com.example.gerenciador.category.entity.Category;
import com.example.gerenciador.category.repository.CategoryRepository;
import com.example.gerenciador.exceptions.CategoryNotFoundException;
import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.exceptions.ProductNotFoundExeption;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.history.entity.HistoryAction;
import com.example.gerenciador.history.service.HistoryService;
import com.example.gerenciador.products.dto.ProductDeleteRequest;
import com.example.gerenciador.products.dto.ProductRequest;
import com.example.gerenciador.products.dto.ProductResponse;
import com.example.gerenciador.products.dto.ProductUpdateRequest;
import com.example.gerenciador.products.entity.Product;
import com.example.gerenciador.products.mapper.ProductMapper;
import com.example.gerenciador.products.repository.ProductRepository;
import com.example.gerenciador.purchase.repository.PurchaseItensRepository;
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
public class ProductService {
    private final ProductRepository productRepository;
    private final SecurityService securityService;
    private final GlobalHelperService globalHelperService;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final PurchaseItensRepository purchaseItensRepository;
    private final HistoryService historyService;

    // ================ GET ======================

    public Page<ProductResponse> getMyProducts(Long familyId, Pageable pageable){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // so membros podem ver os produtos
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return productRepository.findByFamilyIdAndActiveTrue(familyId, pageable)
                .map(productMapper::toProductResponse);

    }


    // ================ POST ======================

    // membro admin da familia criar produtos
    @Transactional
    public ProductResponse createProduct(Long familyId, ProductRequest request){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);


        // verifica se o user é admin e pertence aquela familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // verificar se a categoria pertence a aquela familia do user que ta adcionando
        Category category = categoryRepository.findByIdAndFamilyId(request.categoryId(), familyId)
                .orElseThrow(CategoryNotFoundException::new);

        if (category.getActive() == false){
            throw new CategoryNotFoundException();
        }

        // cria o produto

        Product products = new Product();

        products.setName(request.name());
        products.setCategory(category);
        products.setFamily(family);
        products.setActive(true);

        String message = "criou o produto " + products.getName();
        historyService.createHistory(message, family, loggedUser, HistoryAction.CREATED_PRODUCT);

        return productMapper.toProductResponse(productRepository.save(products));
    }


    // ================ PUT ======================

    // membro admin editar produtos
    @Transactional
    public ProductResponse updateProduct(Long familyId, Long productId, ProductUpdateRequest request){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o user é admin e pertence aquela familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // verificar se a categoria pertence a aquela familia do user que ta adcionando

        // acha o produto
        Product product = productRepository.findByIdAndFamilyId(productId, familyId)
                .orElseThrow(ProductNotFoundExeption::new);


        if (purchaseItensRepository.existsByProductId(productId)){
            throw new ConflictException("Não é possível editar um produto que está em uma compra ");
        }

        String name = product.getName();

        if (request.categoryId()!= null){
            Category category = categoryRepository.findByIdAndFamilyId(request.categoryId(), familyId)
                    .orElseThrow(CategoryNotFoundException::new);

            if (category.getActive() == false){
                throw new CategoryNotFoundException();
            }

            String message = "editou a categoria do produto " + name + " para " + category;
            historyService.createHistory(message, family, loggedUser, HistoryAction.UPDATED_PRODUCT);

            product.setCategory(category);
        }

        if (request.name() != null){
            product.setName(request.name());

            String message = "editou o nome do produto " + name + " para " + product.getName();
            historyService.createHistory(message, family, loggedUser, HistoryAction.UPDATED_PRODUCT);
        }



        return productMapper.toProductResponse(productRepository.save(product));
    }


    // ================ DELETE ======================

    // membro admin deletar produtos
    @Transactional
    public void deleteProduct (Long familyId, Long productId){
        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o user é admin e pertence aquela familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // acha o produto
        Product product = productRepository.findByIdAndFamilyId(productId, familyId)
                .orElseThrow(ProductNotFoundExeption::new);

        // apaga
        product.setActive(false);

        String message = "excluiu o produto " + product.getName();
        historyService.createHistory(message, family, loggedUser, HistoryAction.DELETED_PRODUCT);

        productRepository.save(product);
    }

    // membro admin pode deletar varios produtos
    public void deleteProducts (Long familyId, ProductDeleteRequest request){
        if(request.ids().size() != new HashSet<>(request.ids()).size()){
            throw new ConflictException(
                    "Existem IDs repetidos na requisição"
            );
        }

        User loggedUser = securityService.getLoggedUser();

        // encontrar a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o user é admin e pertence aquela familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        List<Product> products = productRepository.findAllByIdInAndFamilyId(request.ids(), familyId);

        if (products.size() != request.ids().size()){
            throw new ProductNotFoundExeption();
        }


        products.forEach(p -> {
            p.setActive(false);

            historyService.createHistory(
                    "removeu o produto " + p.getName(),
                    family,
                    loggedUser,
                    HistoryAction.DELETED_PRODUCT
            );
        });

        productRepository.saveAll(products);
    }
}
