package com.example.gerenciador.purchase.mapper;

import com.example.gerenciador.purchase.dto.PurchaseItensResponse;
import com.example.gerenciador.purchase.dto.PurchaseResponse;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.purchase.entity.PurchaseItens;
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
                p.getProducts().getId(),
                p.getProducts().getName(),
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
                            item.getProducts().getId(),
                            item.getProducts().getName(),
                            item.getUnitPrice(),
                            item.getQuantity(),
                            subTotal
                    );
                })
                .toList();
    }
}
