package com.example.gerenciador.shoppinglist.controller;


import com.example.gerenciador.shoppinglist.dto.*;
import com.example.gerenciador.shoppinglist.service.ShoppingListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shopping-list")
public class ShoppingListController {
    private final ShoppingListService shoppingListService;

    // ================ GET ======================

    @GetMapping("/my/family/{familyId}")
    public ResponseEntity<Page<ShoppingListResponse>> getMyShoppingLists (
            @PathVariable Long familyId,
            @PageableDefault(
                    size = 12,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ){
        return ResponseEntity.ok(shoppingListService.getMyShoppingLists(familyId, pageable));
    }

    @GetMapping("/my/family/{familyId}/list/{shoppingListId}")
    public ResponseEntity<Page<LIstItemResponse>> getItemsInList(
            @PathVariable Long familyId,
            @PathVariable Long shoppingListId,
            @PageableDefault(
                    size = 12,
                    sort = "name",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ){
        return ResponseEntity.ok(shoppingListService.getItemsInList(familyId, shoppingListId, pageable));
    }


    // ================ POST ======================

    @PostMapping("/new/family/{familyId}")
    public ResponseEntity<ShoppingListResponse> createShoppingList(
            @PathVariable Long familyId,
            @Valid @RequestBody ShoppingListRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(shoppingListService.createShoppingList(familyId, request));
    }


    @PostMapping("/new/family/{familyId}/list/{shoppingListId}/add")
    public ResponseEntity<LIstItemResponse> createShoppingList(
            @PathVariable Long familyId,
            @PathVariable Long shoppingListId,
            @Valid @RequestBody ListItemRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(shoppingListService.addNewItemToList(familyId, shoppingListId, request));
    }


    // ================ PUT ======================

    @PutMapping("/update/family/{familyId}/list/{shoppingListId}")
    ResponseEntity<ShoppingListResponse> updateShoppingList(
            @PathVariable Long familyId,
            @PathVariable Long shoppingListId,
            @Valid ShoppingListUpdateRequest request
            ){
        return ResponseEntity.ok(shoppingListService.updateShoppingList(familyId,shoppingListId, request));
    }

    @PutMapping("/update/family/{familyId}/list/{shoppingListId}/item/{itemId}")
    ResponseEntity<LIstItemResponse> updateitemInList(
            @PathVariable Long familyId,
            @PathVariable Long shoppingListId,
            @PathVariable Long itemId,
            @Valid ListItemUpdateRequest request
    ){
        return ResponseEntity.ok(shoppingListService.updateItemInList(familyId,shoppingListId, itemId, request));
    }

    @PutMapping("/update/family/{familyId}/list/{shoppingListId}/item/{itemId}/done")
    public ResponseEntity<LIstItemResponse> updateDoneStatus(
            @PathVariable Long familyId,
            @PathVariable Long shoppingListId,
            @PathVariable Long itemId,
            @Valid DoneRequest request
    ){
        return ResponseEntity.ok(shoppingListService.updateDoneStatus(familyId, shoppingListId, itemId, request));
    }


    // ================ DELETE ======================





}
