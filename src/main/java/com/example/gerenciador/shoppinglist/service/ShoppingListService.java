package com.example.gerenciador.shoppinglist.service;


import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.exceptions.ItemListNotFoundException;
import com.example.gerenciador.exceptions.ShoppingListNotFoundException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.shoppinglist.dto.*;
import com.example.gerenciador.shoppinglist.entity.ListItem;
import com.example.gerenciador.shoppinglist.entity.PriorityList;
import com.example.gerenciador.shoppinglist.entity.ShoppingList;
import com.example.gerenciador.shoppinglist.mapper.ShoppingListMapper;
import com.example.gerenciador.shoppinglist.repository.ListItemRepository;
import com.example.gerenciador.shoppinglist.repository.ShoppingListRepository;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ListItemRepository listItemRepository;


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

    // ver produtos dentro da lista

    // ================ POST ======================

    // membro admin pode criar lista de compras
    @Transactional
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
    @Transactional
    public LIstItemResponse addNewItemToList(
            Long familyId, Long shoppingListId, ListItemRequest request){

        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca lista
        ShoppingList shoppingList = globalHelperService.getShoppingListOrThrow(familyId, shoppingListId);

        ListItem listItem = new ListItem();

        listItem.setName(request.name());

        listItem.setPriorityList(
                request.priority() != null
                        ? request.priority()
                        : PriorityList.NORMAL
        );

        listItem.setDone(request.done());
        listItem.setShoppingList(shoppingList);

        return shoppingListMapper.toLIstItemResponse(listItemRepository.save(listItem));

    }


    // ================ PUT ======================

    // membro admin pode editar lista de compras
    @Transactional
    public ShoppingListResponse updateShoppingList(
            Long familyId , Long shoppingListId , ShoppingListUpdateRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca lista
        ShoppingList shoppingList = globalHelperService.getShoppingListOrThrow(familyId, shoppingListId);

        // atualiza
        if (request.name() != null){
            shoppingList.setName(request.name());
        }

        return shoppingListMapper.toShoppingListResponse(shoppingListRepository.save(shoppingList));


    }

    // editar produtos dentro da lista
    @Transactional
    public LIstItemResponse updateItemInList(
            Long familyId, Long shoppingListId, Long itemId, ListItemUpdateRequest request
    ){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca lista
        globalHelperService.getShoppingListOrThrow(familyId, shoppingListId);

        // busca o item
        ListItem item = globalHelperService.getListItemOrThrow(itemId, shoppingListId);

        if (request.name() != null){
            item.setName(request.name());
        }

        if (request.priority() != null){
            item.setPriorityList(request.priority());
        }

        return shoppingListMapper.toLIstItemResponse(listItemRepository.save(item));
    }


    // marcar como concluido ou desmarcar
    @Transactional
    public LIstItemResponse updateDoneStatus(
            Long familyId, Long shoppingListId, Long itemId, DoneRequest request
    ){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca lista
        globalHelperService.getShoppingListOrThrow(familyId, shoppingListId);

        ListItem item = globalHelperService.getListItemOrThrow(itemId, shoppingListId);

        item.setDone(request.done());

        return shoppingListMapper.toLIstItemResponse(listItemRepository.save(item));
    }


    // ================ DELETE ======================

    // membro admin pode deletar lista de compras
    @Transactional
    public void deleteShoppingList(Long familyId ,Long shoppingListId){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca a lista
        ShoppingList shoppingList = globalHelperService.getShoppingListOrThrow(familyId, shoppingListId);

        shoppingListRepository.delete(shoppingList);

    }


    // membro admin pode apagar varias listas
    @Transactional
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

    // deletar produto dentro da lista
    @Transactional
    public void delteItemInList(
            Long familyId, Long shoppingListId, Long itemId
    ){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca lista
        globalHelperService.getShoppingListOrThrow(familyId, shoppingListId);

        ListItem item = globalHelperService.getListItemOrThrow(itemId, shoppingListId);

        listItemRepository.delete(item);
    }

    // deletar varios produtos dentro da lista
    public void deleteManyItemsInList(
            Long familyId, Long shoppingListId, ListItemDeleteRequest request
    ){
        if (request.ids().size() != new HashSet<>(request.ids()).size()){
            throw new ConflictException("Há ids repetidos na requisição");
        }

        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin dela
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca lista
        globalHelperService.getShoppingListOrThrow(familyId, shoppingListId);

        List<ListItem> items = listItemRepository.findByShoppingListIdAndIdIn(shoppingListId, request.ids());

        if (request.ids().size() != items.size()){
            throw new ItemListNotFoundException();
        }

        listItemRepository.deleteAll(items);


    }







}
