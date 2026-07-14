package com.example.gerenciador.transaction.service;

import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.exceptions.TransactionNotFoundException;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.history.entity.HistoryAction;
import com.example.gerenciador.history.service.HistoryService;
import com.example.gerenciador.purchase.dto.PurchaseTransactionRequest;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.transaction.dto.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final SecurityService securityService;
    private final GlobalHelperService globalHelperService;
    private final TransactionMapper transactionMapper;
    private final HistoryService historyService;


    // ================ GET ======================

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

    // ver detalhes de uma transação so se ela for do tipo exepense, mostrar os produtos da purhcase
    // e seus respectivos valores
    public TransactionInfoResponse getTransactionDetails(Long familyId, Long transactionId){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a familia existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é membro dela
        globalHelperService.getMemberOrThrow(family, loggedUser);

        // verificar se a transação existe
        Transaction transaction = transactionRepository.findWithDetailsByFamilyIdAndId(familyId, transactionId)
                .orElseThrow(TransactionNotFoundException::new);

        return transactionMapper.toTransactionInfoResponse(transaction);
    }


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


    // msotra o numero total de transaões feita pela família
    public Long totalTransactions(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a familia existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é membro dela
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return transactionRepository.countByFamilyId(familyId);
    }



    // pegar valor total de transações do tipo income e exepense
    public TransactionBalanceMonthResponse getBalanceByMonth(
            Long familyId,
            int year,
            int month
    ) {
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1);

        return transactionRepository.getBalanceByMonth(
                familyId,
                start,
                end
        );
    }


    // filtrar transações
    public Page<TransactionResponse> transactionSearch(
            Long familyId,
            String title,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {

        User user = securityService.getLoggedUser();

        Family family = globalHelperService.getFamilyOrThrow(familyId);

        globalHelperService.getMemberOrThrow(family, user);


        LocalDateTime start = null;
        LocalDateTime end = null;

        if(startDate != null){
            start = startDate.atStartOfDay();
        }

        if(endDate != null){
            end = endDate.plusDays(1).atStartOfDay();
        }


        return transactionRepository.search(
                familyId,
                title,
                type,
                start,
                end,
                pageable
        ).map(transactionMapper::toTransactionResponse);
    }

    // ================ POST ======================

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

        String message = "criou uma nova transação de entrada " + transaction.getTitle() + ", no valor de R$ " + request.ammount();
        historyService.createHistory(message, family, loggedUser, HistoryAction.CREATED_TRANSACTION);


        return transactionMapper.toTransactionResponse(transactionRepository.save(transaction));

    }


    // criar a trnasação de gasto (metodo exclusivo para closePurchase de PurchaseService)
    public TransactionResponse createExpenseTransaction(
            Purchase purchase, BigDecimal total, PurchaseTransactionRequest request){

        User loggedUser = securityService.getLoggedUser();

        // cria a transação
        Transaction transaction = new Transaction();

        transaction.setTitle(purchase.getName());
        transaction.setPurchase(purchase);
        transaction.setFamily(purchase.getFamily());
        transaction.setDateTime(LocalDateTime.now());
        transaction.setAmount(total);
        transaction.setTransactionType(TransactionType.EXPENSE);

        // descrição da transação é opcional
        transaction.setDescription(request.description());

        String message = "criou uma nova transação de saída " + transaction.getTitle() + ", no valor de R$ " + total;
        historyService.createHistory(message
                , purchase.getFamily(),
                loggedUser, HistoryAction.CREATED_TRANSACTION);

        return transactionMapper.toTransactionResponse(transactionRepository.save(transaction));
    }
}
