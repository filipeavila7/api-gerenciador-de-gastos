package com.example.gerenciador.transaction.service;

import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.security.SecurityService;
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
}
