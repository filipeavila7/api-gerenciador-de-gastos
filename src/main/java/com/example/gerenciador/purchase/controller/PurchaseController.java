package com.example.gerenciador.purchase.controller;

import com.example.gerenciador.purchase.dto.*;
import com.example.gerenciador.purchase.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;


    // ================ ROTAS ADMIN ======================



    // ================ GET ======================


    // ================ POST ======================


    // ================ PUT ======================


    // ================ DELETE ======================



    // ================ ROTAS USER ======================


    // ================ GET ======================

    @GetMapping("/my/family/{familyId}")
    public ResponseEntity<Page<PurchaseResponse>> getMyPurchases(
            @PathVariable Long familyId,
            @PageableDefault(size = 12, sort = "dateTime", direction = Sort.Direction.DESC)
            Pageable pageable
            ){
        return ResponseEntity.ok(purchaseService.getMyPurchases(familyId, pageable));

    }

    @GetMapping("/my/family/{familyId}/purchase/{purchaseId}")
    public ResponseEntity<PurchaseResponse> getPurchaseById(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId
    ){
        return ResponseEntity.ok(purchaseService.getPurchaseById(familyId, purchaseId));
    }


    @GetMapping("/my/family/{familyId}/purchase/{purchaseId}/itens")
    public ResponseEntity<Page<PurchaseItensResponse>> getMyProductsInPurchase(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId,
            @PageableDefault(size = 12, sort = "product.name", direction = Sort.Direction.ASC)
            Pageable pageable
    ){
        return ResponseEntity.ok(purchaseService.getMyProductsInPurchase(familyId, purchaseId,pageable));

    }



    // ================ POST ======================

    @PostMapping("/new/family/{familyId}")
    public ResponseEntity<PurchaseResponse> createPurchase(
            @PathVariable Long familyId,
            @Valid @RequestBody PurchaseRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseService.createPurchase(familyId, request));
    }

    @PostMapping("/add/family/{familyId}/purchase/{purchaseId}")
    public ResponseEntity<PurchaseItensResponse> addProductToPurchase(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId,
            @Valid @RequestBody PurchaseItensRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseService.addProductToPurchase(familyId, purchaseId, request));
    }


    @PostMapping("/add/family/{familyId}/purchase/{purchaseId}/many")
    public ResponseEntity<List<PurchaseItensResponse>> addManyProductsToPurchase(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId,
            @Valid @RequestBody PurchaseManyItensRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseService.addManyProductsToPurchase(familyId, purchaseId, request));
    }




    // ================ PUT ======================

    @PutMapping("/update/family/{familyId}/purchase/{purchaseId}")
    public ResponseEntity<PurchaseResponse> updatePurchase(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId,
            @Valid @RequestBody PurchaseUpdateRequest request
    ){
        return ResponseEntity.ok(purchaseService.updatePurchase(familyId, purchaseId, request));
    }


    @PutMapping("/update/family/{familyId}/purchase/{purchaseId}/product/{productId}")
    public ResponseEntity<PurchaseItensResponse> updateItemInPurchase(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId,
            @PathVariable Long productId,
            @Valid @RequestBody PurchaseItenUpdateRequest request
    ){
        return ResponseEntity.ok(purchaseService.updateItemInPurchase(familyId, purchaseId, productId, request));
    }

    // ================ PATCH ======================

    @PatchMapping("/close/family/{familyId}/purchase/{purchaseId}")
    public ResponseEntity<PurchaseTransactionResponse> closePurchase(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId,
            @RequestBody PurchaseTransactionRequest request
    ){
        return ResponseEntity.ok(purchaseService.closePurchase(familyId, purchaseId, request));
    }


    // ================ DELETE ======================

    @DeleteMapping("/delete/family/{familyId}/purchase/{purchaseId}")
    public ResponseEntity<Void> deletePurchase(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId
    ){
        purchaseService.deletePurchase(familyId, purchaseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/family/{familyId}/purchase/many")
    public ResponseEntity<Void> deletePurchase(
            @PathVariable Long familyId,
            @Valid @RequestBody DeleteManyRequest request
    ){
        purchaseService.deleteManyPurchases(familyId, request);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/delete/family/{familyId}/purchase/{purchaseId}/product/{productId}")
    public ResponseEntity<Void> deleteProductInPurchase(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId,
            @PathVariable Long productId
    ){
        purchaseService.deleteProductInPurchase(familyId, purchaseId, productId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/delete/family/{familyId}/purchase/{purchaseId}/product/many")
    public ResponseEntity<Void> deleteManyProductsInPurchase(
            @PathVariable Long familyId,
            @PathVariable Long purchaseId,
            @Valid @RequestBody DeleteManyRequest request
    ){
        purchaseService.deleteManyProductsInPurchase(familyId, purchaseId,request);
        return ResponseEntity.noContent().build();
    }


}
