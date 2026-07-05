package com.example.gerenciador.transaction.mapper;

import com.example.gerenciador.purchase.dto.PurchaseItensResponse;
import com.example.gerenciador.purchase.mapper.PurchaseMapper;
import com.example.gerenciador.transaction.dto.BalanceResponse;
import com.example.gerenciador.transaction.dto.TransactionInfoResponse;
import com.example.gerenciador.transaction.dto.TransactionResponse;
import com.example.gerenciador.transaction.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionMapper {
    private final PurchaseMapper purchaseMapper;

    public TransactionResponse toTransactionResponse(Transaction t){
        return new TransactionResponse(
                t.getId(),
                t.getFamily().getId(),
                t.getPurchase() != null ?
                t.getPurchase().getId() : null,
                t.getTitle(),
                t.getAmount(),
                t.getTransactionType(),
                t.getDateTime(),
                t.getDescription()
        );
    }

    public BalanceResponse toBalanceResponse(BigDecimal b){
        return new BalanceResponse(
                b
        );
    }


    public TransactionInfoResponse toTransactionInfoResponse(Transaction t){
        // cpnversão de intens caso a transaction seja do tipo expense (purhcaseId != null)
        List<PurchaseItensResponse> itens =
                t.getPurchase() == null
                        ? null
                        : t.getPurchase()
                        .getItens()
                        .stream()
                        .map(purchaseMapper::toPurchaseItensResponse)
                        .toList();

        return new TransactionInfoResponse(
                t.getId(),
                t.getFamily().getId(),
                t.getPurchase() != null ?
                        t.getPurchase().getId()
                        : null,
                t.getTitle(),
                t.getAmount(),
                t.getDescription(),
                t.getDateTime(),
                t.getTransactionType(),
                itens

        );
    }
}
