package com.example.gerenciador.graphics.controller;

import com.example.gerenciador.category.dto.CategoryExpenseResponse;
import com.example.gerenciador.graphics.service.GraphicService;
import com.example.gerenciador.products.dto.ProductExpenseResponse;
import com.example.gerenciador.transaction.dto.TransactionMonthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/graphic")
public class GraphicController {
    private final GraphicService graphicService;

    @GetMapping("/family/{familyId}/category-expenses")
    public ResponseEntity<List<CategoryExpenseResponse>> getCategoryExepense
            (@PathVariable Long familyId){
        return ResponseEntity.ok(graphicService.getCategoryExpense(familyId));
    }


    @GetMapping("/category-expenses")
    public ResponseEntity<List<CategoryExpenseResponse>> getCategoryExpenses(
            @RequestParam Long familyId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return ResponseEntity.ok(graphicService.getCategoryExpenseByMonth(familyId, year, month));
    }


    @GetMapping("/product-expenses")
    public ResponseEntity<List<ProductExpenseResponse>> getProductExpenses(
            @RequestParam Long familyId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return ResponseEntity.ok(graphicService.getProductExpenseByMonth(familyId, year, month));
    }


    @GetMapping("/transaction-expenses")
    public ResponseEntity<List<TransactionMonthResponse>> getTransactionExpenses(
            @RequestParam Long familyId,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(graphicService.getTransactionExpenseByMonth(familyId, year));
    }




}
