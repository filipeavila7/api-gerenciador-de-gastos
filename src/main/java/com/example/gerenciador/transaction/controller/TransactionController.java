package com.example.gerenciador.transaction.controller;


import com.example.gerenciador.transaction.dto.BalanceResponse;
import com.example.gerenciador.transaction.dto.TransactionIncomeRequest;
import com.example.gerenciador.transaction.dto.TransactionInfoResponse;
import com.example.gerenciador.transaction.dto.TransactionResponse;
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


    @GetMapping("/get/balance/family/{familyId}")
    public ResponseEntity<BalanceResponse> getMybalance(
            @PathVariable Long familyId
    ){
        return ResponseEntity.ok(transactionService.getMyBalance(familyId));
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
