package com.example.gerenciador.purchase.service;

import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.products.entity.Product;
import com.example.gerenciador.purchase.dto.PurchaseItensRequest;
import com.example.gerenciador.purchase.dto.PurchaseItensResponse;
import com.example.gerenciador.purchase.dto.PurchaseRequest;
import com.example.gerenciador.purchase.dto.PurchaseResponse;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.purchase.entity.PurchaseItens;
import com.example.gerenciador.purchase.mapper.PurchaseMapper;
import com.example.gerenciador.purchase.repository.PurchaseItensRepository;
import com.example.gerenciador.purchase.repository.PurchaseRepository;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


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

    // ================ PUT ======================

    // ================ DELETE ======================
}
