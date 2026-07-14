package com.example.gerenciador.transaction.controller;


import com.example.gerenciador.transaction.dto.*;
import com.example.gerenciador.transaction.entity.TransactionType;
import com.example.gerenciador.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;


    // ================ GET ======================

    @GetMapping("/my/family/{familyId}")
    public ResponseEntity<Page<TransactionResponse>>getMyTransactions(
            @PathVariable Long familyId,
            @PageableDefault(size = 12, sort = "dateTime", direction = Sort.Direction.DESC)
            Pageable pageable){
        return ResponseEntity.ok(transactionService.getMyTransactions(familyId, pageable));

    }

    @GetMapping("get/transaction/{transactionId}/family/{familyId}")
    public ResponseEntity<TransactionInfoResponse> getTransactionDetails(
            @PathVariable Long familyId,
            @PathVariable Long transactionId
    ){
        return ResponseEntity.ok(transactionService.getTransactionDetails(familyId, transactionId));
    }


    @GetMapping("/total/family/{familyId}")
    public ResponseEntity<Long> getTotalTransactions(
            @PathVariable Long familyId
    ){
        return ResponseEntity.ok(transactionService.totalTransactions(familyId));
    }


    @GetMapping("/get/balance/family/{familyId}")
    public ResponseEntity<BalanceResponse> getMybalance(
            @PathVariable Long familyId
    ){
        return ResponseEntity.ok(transactionService.getMyBalance(familyId));
    }

    // /transactions/balance/family/1?year=2026&month=7
    @GetMapping("/balance/family/{familyId}")
    public ResponseEntity<TransactionBalanceMonthResponse> getBalanceByMonth(
            @PathVariable Long familyId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(
                transactionService.getBalanceByMonth(familyId, year, month)
        );
    }


    @GetMapping("/my/family/{familyId}/search")
    public ResponseEntity<Page<TransactionResponse>> search(
            @PathVariable Long familyId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(
                    size = 12,
                    sort = "dateTime",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                transactionService.transactionSearch(
                        familyId,
                        title,
                        type,
                        startDate,
                        endDate,
                        pageable
                )
        );
    }



    // ================ POST ======================

    @PostMapping("/new/family/{familyId}")
    public ResponseEntity<TransactionResponse> createIncomeTransaction(
            @PathVariable Long familyId,
            @Valid @RequestBody TransactionIncomeRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createIncomeTransaction(familyId, request));
    }
}
