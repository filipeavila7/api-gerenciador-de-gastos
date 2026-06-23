package com.example.gerenciador.transaction.mapper;

import com.example.gerenciador.transaction.dto.BalanceResponse;
import com.example.gerenciador.transaction.dto.TransactionResponse;
import com.example.gerenciador.transaction.entity.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionMapper {
    public TransactionResponse toTransactionResponse(Transaction t){
        return new TransactionResponse(
                t.getId(),
                t.getFamily().getId(),
                t.getPurchase() != null ?
                t.getPurchase().getId() : null,
                t.getTitle(),
                t.getAmount(),
                t.getTransactionType(),
                t.getDateTime()
        );
    }

    public BalanceResponse toBalanceResponse(BigDecimal b){
        return new BalanceResponse(
                b
        );
    }
}
