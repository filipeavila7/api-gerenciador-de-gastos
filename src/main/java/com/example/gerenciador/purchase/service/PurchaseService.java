package com.example.gerenciador.purchase.service;

import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.products.entity.Product;
import com.example.gerenciador.purchase.dto.*;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.purchase.entity.PurchaseItens;
import com.example.gerenciador.purchase.mapper.PurchaseMapper;
import com.example.gerenciador.purchase.repository.PurchaseItensRepository;
import com.example.gerenciador.purchase.repository.PurchaseRepository;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final GlobalHelperService globalHelperService;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItensRepository purchaseItensRepository;
    private final SecurityService securityService;
    private final PurchaseMapper purchaseMapper;

    // ================ GET ======================


    // ================ POST ======================

    // membro admin criar bloco de compra / maleta
    @Transactional
    public PurchaseResponse createPurchase (Long familyId, PurchaseRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // cria o bloco
        Purchase purchase = new Purchase();

        purchase.setName(request.name());
        purchase.setFamily(family);
        purchase.setDateTime(LocalDateTime.now());

        purchaseRepository.save(purchase);

        return purchaseMapper.toPurchaseResponse(purchase);

    }

    // membro admin adcionar produto no bloco
    @Transactional
    public PurchaseItensResponse addProductToPurchase(Long familyId, Long purchaseId, PurchaseItensRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca o produto e verifica se ele existe
        Product product = globalHelperService.getProductOrThrow(familyId, request.productId());

        // busca a purchase e verifica se ela existe e pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        // verifica se o produto ja existe dentro da maleta purchaseItens
       if (purchaseItensRepository.existsByProductIdAndPurchaseId(request.productId(), purchaseId)){
           throw new ConflictException("Esse produto ja existe na compra");
       }

        // criar o vinculo entre purchase e produto -> purchaseItens
        PurchaseItens purchaseItens = new PurchaseItens();

        purchaseItens.setProducts(product);
        purchaseItens.setPurchase(purchase);

        purchaseItens.setQuantity(request.quantity());
        purchaseItens.setUnitPrice(request.unitPrice());

        // adcionar do outro lado da relação
        purchase.getItens().add(purchaseItens);

        purchaseItensRepository.save(purchaseItens);

        return purchaseMapper.toPurchaseItensResponse(purchaseItens);


    }

    @Transactional
    public List<PurchaseItensResponse> addManyProductsToPurchase(Long familyId, Long purchaseId, PurchaseManyItensRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // extrair a lista de ids dos produtos do request
        List<Long> productsIds = request.itensRequests().stream()
                .map(PurchaseItensRequest::productId)
                .toList();

        // verifica se eles existem
        List<Product> products = globalHelperService.getManyProductOrThrow(productsIds, familyId);

        // busca a purchase e verifica se ela existe e pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        // verificar se não tem ids repetidos
        if(productsIds.size() != new HashSet<>(productsIds).size()){
            throw new ConflictException(
                    "Existem produtos repetidos na requisição"
            );
        }

        // verifica se ja existe
         if (purchaseItensRepository.existsByPurchaseIdAndProductsIdIn(purchaseId, productsIds)){
             throw new ConflictException("Já existem produtos nessa compra");
         }


         // criar um map para pegar os produtos rapidamente
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        p -> p
                ));

         // percorrer o request e salavr todos de uma vez
        List<PurchaseItens> purchaseItensList = request.itensRequests().stream()
                .map(itemRequest -> {
                            Product product = productMap.get(itemRequest.productId());// para cada item request, pegar o produto pelo map, pelo id

                            // cria o vinculo
                            PurchaseItens item = new PurchaseItens();
                            item.setPurchase(purchase);
                            item.setProducts(product);
                            item.setQuantity(itemRequest.quantity());
                            item.setUnitPrice(itemRequest.unitPrice());

                            return item; // retorna para adcionar na lista
                        }

                ).toList();

        return purchaseMapper.toPurchaseManyItensResponse(purchaseItensRepository
                        .saveAll(purchaseItensList));

    }

    // ================ PUT ======================

    // ================ DELETE ======================
}
