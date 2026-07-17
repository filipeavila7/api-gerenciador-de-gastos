package com.example.gerenciador.graphics.service;


import com.example.gerenciador.category.dto.CategoryExpenseResponse;
import com.example.gerenciador.category.repository.CategoryRepository;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.graphics.repository.GraphicRepository;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.products.dto.ProductExpenseResponse;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.transaction.dto.TransactionMonthResponse;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphicService {
    private final GlobalHelperService globalHelperService;
    private final SecurityService securityService;
    private final GraphicRepository graphicRepository;



    // retornar dados para o grafico de gastos por cada categoria
    public List<CategoryExpenseResponse> getCategoryExpense(Long familyId){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a familia existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuário é membro dela
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return graphicRepository.getExpensesByCategory(familyId);

    }


    // retornar dados para o grafico de gastos por cada categoria com base no mes da compra
    public List<CategoryExpenseResponse> getCategoryExpenseByMonth(
            Long familyId, Integer year, Integer month){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a familia existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuário é membro dela
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return graphicRepository.getCategoryExpensesByMonth(familyId, year, month);

    }


    // retornar o valor total de gastos por cada mes
    public List<TransactionMonthResponse> getTransactionExpenseByMonth(
            Long familyId, Integer year
    ){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a familia existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuário é membro dela
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return graphicRepository.getMonthlyExpenses(familyId, year);
    }

    // retoornar os dados para o gráfico de gasto com base nos produtos por mes e ano
    public List<ProductExpenseResponse> getProductExpenseByMonth(
            Long familyId, Integer year, Integer month
    ){
        User loggedUser = securityService.getLoggedUser();

        // verifica se a familia existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuário é membro dela
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return graphicRepository.getProductExpensesByMonth(familyId, year, month);
    }
}
