package com.example.gerenciador.shoppinglist.service;


import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.exceptions.ShoppingListNotFoundException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.shoppinglist.dto.ShoppingListDeleteRequest;
import com.example.gerenciador.shoppinglist.dto.ShoppingListRequest;
import com.example.gerenciador.shoppinglist.dto.ShoppingListResponse;
import com.example.gerenciador.shoppinglist.dto.ShoppingListUpdateRequest;
import com.example.gerenciador.shoppinglist.entity.ShoppingList;
import com.example.gerenciador.shoppinglist.mapper.ShoppingListMapper;
import com.example.gerenciador.shoppinglist.repository.ShoppingListRepository;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final GlobalHelperService globalHelperService;
    private final SecurityService securityService;
    private final ShoppingListMapper shoppingListMapper;


    // ================ GET ======================

    // membros podem ver todas as listas da familia
    public Page<ShoppingListResponse> getMyShoppingLists(Long familyId, Pageable pageable){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é membro da familia
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return shoppingListRepository.findAllByFamilyId(familyId, pageable)
                .map(shoppingListMapper::toShoppingListResponse);
    }

    // ================ POST ======================

    // membro admin pode criar lista de compras
    public ShoppingListResponse createShoppingList(Long familyId, ShoppingListRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        ShoppingList shoppingList = new ShoppingList();

        shoppingList.setCreatedAt(LocalDateTime.now());
        shoppingList.setFamily(family);
        shoppingList.setName(request.name());

        return shoppingListMapper.toShoppingListResponse(shoppingListRepository.save(shoppingList));

    }

    // adcionar itens na lista


    // ================ PUT ======================

    // membro admin pode editar lista de compras
    public ShoppingListResponse updateShoppingList(
            Long familyId , Long shoppingListId , ShoppingListUpdateRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca lista
        ShoppingList shoppingList = shoppingListRepository.findByFamilyIdAndId(familyId, shoppingListId)
                .orElseThrow(ShoppingListNotFoundException::new);

        // atualiza
        if (request.name() != null){
            shoppingList.setName(request.name());
        }

        return shoppingListMapper.toShoppingListResponse(shoppingListRepository.save(shoppingList));
    }


    // ================ DELETE ======================

    // membro admin pode deletar lista de compras
    public void deleteShoppingList(Long familyId ,Long shoppingListId){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);


        ShoppingList shoppingList = shoppingListRepository.findByFamilyIdAndId(familyId, shoppingListId)
                        .orElseThrow(ShoppingListNotFoundException::new);

        shoppingListRepository.delete(shoppingList);
    }


    // membro admin pode apagar varias listas
    public void deleteManyShoppingLists(Long familyId, ShoppingListDeleteRequest request){
        // evitar repetidos
        if (request.ids().size() != new HashSet<>(request.ids()).size()){
            throw new ConflictException("Ha Listas repetidas na requisição");
        }

        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // pegar listas
        List<ShoppingList> shoppingLists = shoppingListRepository
                .findAllByFamilyIdAndIdIn(familyId, request.ids());

        // caso falte alguns lança exceção
        if (shoppingLists.size() != request.ids().size()){
            throw new ShoppingListNotFoundException();
        }

        shoppingListRepository.deleteAll(shoppingLists);


    }





}
