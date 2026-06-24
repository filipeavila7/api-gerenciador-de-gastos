package com.example.gerenciador.helpers;

import com.example.gerenciador.exceptions.*;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.family.entity.FamilyMember;
import com.example.gerenciador.family.entity.FamilyRole;
import com.example.gerenciador.family.repository.FamilyMemberRepository;
import com.example.gerenciador.family.repository.FamilyRepository;
import com.example.gerenciador.products.entity.Product;
import com.example.gerenciador.products.repository.ProductRepository;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.purchase.repository.PurchaseRepository;
import com.example.gerenciador.shoppinglist.entity.ShoppingList;
import com.example.gerenciador.shoppinglist.repository.ShoppingListRepository;
import com.example.gerenciador.transaction.entity.Transaction;
import com.example.gerenciador.transaction.repository.TransactionRepository;
import com.example.gerenciador.user.entity.User;
import com.example.gerenciador.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GlobalHelperService {
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyRepository familyRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ShoppingListRepository shoppingListRepository;

    // verificar se o usuario é admin da familia e pertence a ela
    public FamilyMember getAdminMemberOrThrow(Family family, User user) {
        FamilyMember member = familyMemberRepository
                .findByFamilyAndUser(family, user)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        if (member.getRole() != FamilyRole.ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        return member;
    }

    // verificar somente se o usuario pertence a familia
    public void getMemberOrThrow(Family family, User user) {
        familyMemberRepository
                .findByFamilyAndUser(family, user)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

    }

    // buscar familia pelo id
    public Family getFamilyOrThrow(Long familyId){
        return familyRepository.findById(familyId)
                .orElseThrow(FamilyNotFoundException::new);
    }

    // buscar produto pelo id do produto e da familia
    public Product getProductOrThrow(Long familyId, Long productId){
        return productRepository.findByIdAndFamilyId(productId, familyId)
                .orElseThrow(ProductNotFoundExeption::new);
    }

    // buscara purchase pelo id da purchase e da familia
    public Purchase getPurchaseOrThrow(Long familyId, Long purchaseId){
        return purchaseRepository.findByIdAndFamilyId(purchaseId, familyId)
                .orElseThrow(PurchaseNotFoundException::new);
    }

    // busca varios produtos
    public List<Product> getManyProductOrThrow(List<Long> ids, Long familyId){
        List<Product> products = productRepository.findAllByIdInAndFamilyIdAndActiveTrue(ids, familyId);

        if (products.size() != ids.size()){
            throw new ProductNotFoundExeption();
        }

        return products;
    }

    // busca um usuario
    public User getUserOrThrow(Long id){
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }


    // busca uma transação
    public Transaction getTransactionOrThrow(Long familyId, Long transactionId){
        return transactionRepository.findByFamilyIdAndId(familyId, transactionId)
                .orElseThrow(TransactionNotFoundException::new);
    }


    // busca uma lista de compras
    public ShoppingList getShoppingListOrThrow(Long familyId, Long shoppingListId){
        return shoppingListRepository.findByFamilyIdAndId(familyId, shoppingListId)
                .orElseThrow(ShoppingListNotFoundException::new);
    }

}
