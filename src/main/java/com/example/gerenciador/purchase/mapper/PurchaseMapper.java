package com.example.gerenciador.purchase.mapper;

import com.example.gerenciador.purchase.dto.PurchaseItensResponse;
import com.example.gerenciador.purchase.dto.PurchaseResponse;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.purchase.entity.PurchaseItens;
import org.springframework.stereotype.Component;

@Component
public class PurchaseMapper {

    public PurchaseResponse toPurchaseResponse(Purchase p){
        return new PurchaseResponse(
                p.getId(),
                p.getName(),
                p.getFamily().getId(),
                p.getDateTime()
        );
    }

    public PurchaseItensResponse toPurchaseItensResponse(PurchaseItens p){
        return new PurchaseItensResponse(
                p.getPurchase().getId(),
                p.getPurchase().getFamily().getId(),
                p.getProducts().getId(),
                p.getProducts().getName(),
                p.getUnitPrice(),
                p.getQuantity()
        );
    }
}
