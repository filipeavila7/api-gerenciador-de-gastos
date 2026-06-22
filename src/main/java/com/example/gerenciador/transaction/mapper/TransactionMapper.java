package com.example.gerenciador.transaction.mapper;

import com.example.gerenciador.transaction.dto.TransactionResponse;
import com.example.gerenciador.transaction.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionResponse toTransactionResponse(Transaction t){
        return new TransactionResponse(
                t.getId(),
                t.getFamily().getId(),
                t.getTitle(),
                t.getAmount(),
                t.getTransactionType(),
                t.getDateTime()
        );
    }
}
