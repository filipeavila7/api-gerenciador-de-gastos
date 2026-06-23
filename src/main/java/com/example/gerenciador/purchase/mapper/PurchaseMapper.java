package com.example.gerenciador.purchase.mapper;

import com.example.gerenciador.purchase.dto.PurchaseItensResponse;
import com.example.gerenciador.purchase.dto.PurchaseResponse;
import com.example.gerenciador.purchase.dto.PurchaseTransactionResponse;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.purchase.entity.PurchaseItens;
import com.example.gerenciador.transaction.dto.TransactionResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PurchaseMapper {

    public PurchaseResponse toPurchaseResponse(Purchase p){
        BigDecimal total = p.getItens()
                .stream()
                .map(item ->
                        item.getUnitPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())) // multiplica o unit price pela quantidade
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add); // soma todos os valores

        return new PurchaseResponse(
                p.getId(),
                p.getName(),
                p.getFamily().getId(),
                p.getDateTime(),
                p.getPurchaseStatus(),
                total
        );
    }

    public PurchaseItensResponse toPurchaseItensResponse(PurchaseItens p){
        BigDecimal subtotal =
                p.getUnitPrice()
                        .multiply(BigDecimal.valueOf(p.getQuantity()));

        return new PurchaseItensResponse(
                p.getPurchase().getId(),
                p.getPurchase().getFamily().getId(),
                p.getProduct().getId(),
                p.getProduct().getName(),
                p.getUnitPrice(),
                p.getQuantity(),
                subtotal
        );
    }

    public List<PurchaseItensResponse> toPurchaseManyItensResponse(List<PurchaseItens> purchaseItens){

        return purchaseItens.stream()
                .map(item -> {

                    BigDecimal subTotal = item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new PurchaseItensResponse(
                            item.getPurchase().getId(),
                            item.getPurchase().getFamily().getId(),
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getUnitPrice(),
                            item.getQuantity(),
                            subTotal
                    );
                })
                .toList();
    }

    public PurchaseTransactionResponse toPurchaseTransactionResponse(Purchase p, TransactionResponse t){
        return new PurchaseTransactionResponse(
                p.getId(),
                p.getFamily().getId(),
                p.getPurchaseStatus(),
                t

        );
    }
}
