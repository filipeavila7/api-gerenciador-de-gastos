package com.example.gerenciador.transaction.service;

import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.transaction.dto.BalanceResponse;
import com.example.gerenciador.transaction.dto.TransactionIncomeRequest;
import com.example.gerenciador.transaction.dto.TransactionResponse;
import com.example.gerenciador.transaction.entity.Transaction;
import com.example.gerenciador.transaction.entity.TransactionType;
import com.example.gerenciador.transaction.mapper.TransactionMapper;
import com.example.gerenciador.transaction.repository.TransactionRepository;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final SecurityService securityService;
    private final GlobalHelperService globalHelperService;
    private final TransactionMapper transactionMapper;


    // membro da familia pode ver todas as transações
    public Page<TransactionResponse> getMyTransactions(Long familyId, Pageable pageable){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a familia existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é membro dela
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return transactionRepository.findAllByFamilyId(familyId, pageable)
                .map(transactionMapper::toTransactionResponse);

    }

    // ver detalhes de uma transação so, se ela for do tipo exepense, mostrar os produtos da purhcase
    // e seus respectivos valores


    // calcular saldo geral da familia (INCOME - EXPENSE)
    public BalanceResponse getMyBalance(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a familia existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é membro dela
        globalHelperService.getMemberOrThrow(family, loggedUser);

        // banco faz o cálculo
        return transactionMapper.toBalanceResponse(transactionRepository.calculateBalance(familyId));
    }


    // membro admin pode criar transações do tipo income
    public TransactionResponse createIncomeTransaction(Long familyId, TransactionIncomeRequest request){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a familia existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verificar se o usuario membro é admin e pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // cria a transação income
        Transaction transaction = new Transaction();

        transaction.setTitle(request.title());
        transaction.setDescription(request.description());
        transaction.setAmount(request.ammount());
        transaction.setDateTime(LocalDateTime.now());
        transaction.setTransactionType(TransactionType.INCOME);
        transaction.setFamily(family);


        return transactionMapper.toTransactionResponse(transactionRepository.save(transaction));

    }


    // criar a trnasação de gasto (metodo exclusivo para closePurchase de PurchaseService)
    public TransactionResponse createExpenseTransaction(Purchase purchase, BigDecimal total){

        // cria a transação
        Transaction transaction = new Transaction();

        transaction.setTitle(purchase.getName());
        transaction.setPurchase(purchase);
        transaction.setFamily(purchase.getFamily());
        transaction.setDateTime(LocalDateTime.now());
        transaction.setAmount(total);
        transaction.setTransactionType(TransactionType.EXPENSE);

        return transactionMapper.toTransactionResponse(transactionRepository.save(transaction));
    }
}
