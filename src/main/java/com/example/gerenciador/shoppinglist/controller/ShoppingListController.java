package com.example.gerenciador.shoppinglist.controller;


import com.example.gerenciador.shoppinglist.dto.LIstItemResponse;
import com.example.gerenciador.shoppinglist.dto.ShoppingListRequest;
import com.example.gerenciador.shoppinglist.dto.ShoppingListResponse;
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




    // ================ PUT ======================

    // ================ DELETE ======================

}
