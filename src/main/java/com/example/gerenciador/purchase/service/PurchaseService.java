package com.example.gerenciador.purchase.service;

import com.example.gerenciador.exceptions.BusinessException;
import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.exceptions.ProductNotFoundExeption;
import com.example.gerenciador.exceptions.PurchaseNotFoundException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.history.entity.HistoryAction;
import com.example.gerenciador.history.service.HistoryService;
import com.example.gerenciador.products.entity.Product;
import com.example.gerenciador.purchase.dto.*;
import com.example.gerenciador.purchase.entity.Purchase;
import com.example.gerenciador.purchase.entity.PurchaseItens;
import com.example.gerenciador.purchase.entity.PurchaseStatus;
import com.example.gerenciador.purchase.mapper.PurchaseMapper;
import com.example.gerenciador.purchase.repository.PurchaseItensRepository;
import com.example.gerenciador.purchase.repository.PurchaseRepository;
import com.example.gerenciador.security.SecurityService;
import com.example.gerenciador.transaction.dto.TransactionResponse;
import com.example.gerenciador.transaction.service.TransactionService;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final GlobalHelperService globalHelperService;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItensRepository purchaseItensRepository;
    private final SecurityService securityService;
    private final PurchaseMapper purchaseMapper;
    private final TransactionService transactionService;
    private final HistoryService historyService;

    // ================ GET ======================


    // retorna uma purchase pelo id
    public PurchaseResponse getPurchaseById(Long familyId, Long purchaseId){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verfica se é membro
        globalHelperService.getMemberOrThrow(family, loggedUser);


        return purchaseMapper.toPurchaseResponse(
                globalHelperService.getPurchaseOrThrow(familyId, purchaseId)
        );


    }


    // retornar todas as compras da familia, apenas para membros dela
    public Page<PurchaseResponse> getMyPurchases(Long familyId, Pageable pageable){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verfica se é membro
        globalHelperService.getMemberOrThrow(family, loggedUser);

        // busca as purchase
        return purchaseRepository.findAllByFamilyId(familyId, pageable)
                .map(purchaseMapper::toPurchaseResponse);


    }

    // membros podem ver todos os produtos dentro da compra da familia
    public Page<PurchaseItensResponse> getMyProductsInPurchase(Long familyId, Long purchaseId, Pageable pageable){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verfica se é membro
        globalHelperService.getMemberOrThrow(family, loggedUser);

        // busca a purchase e verifica se é da familia e se existe
        globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        return purchaseItensRepository.findAllByPurchaseId(purchaseId, pageable)
                .map(purchaseMapper::toPurchaseItensResponse);

    }

    public Page<PurchaseResponse> purchaseSearch(
            Long familyId,
            String name,
            PurchaseStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {

        User user = securityService.getLoggedUser();

        Family family = globalHelperService.getFamilyOrThrow(familyId);

        globalHelperService.getMemberOrThrow(family, user);


        LocalDateTime start = null;
        LocalDateTime end = null;

        if(startDate != null) {
            start = startDate.atStartOfDay();
        }

        if(endDate != null) {
            end = endDate.plusDays(1).atStartOfDay();
        }


        return purchaseRepository.search(
                familyId,
                name,
                status,
                start,
                end,
                pageable
        ).map(purchaseMapper::toPurchaseResponse);
    }


    // ================ POST ======================

    // membro admin criar compra
    @Transactional
    public PurchaseResponse createPurchase (Long familyId, PurchaseRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // cria a compra
        Purchase purchase = new Purchase();

        purchase.setName(request.name());
        purchase.setFamily(family);
        purchase.setDateTime(LocalDateTime.now());
        purchase.setPurchaseStatus(PurchaseStatus.OPEN);
        purchase.setTotal(BigDecimal.ZERO);

        purchaseRepository.save(purchase);

        String message = "criou uma nova compra " + purchase.getName();
        historyService.createHistory(message, family, loggedUser, HistoryAction.CREATED_PURCHASE);

        return purchaseMapper.toPurchaseResponse(purchase);

    }

    // membro admin adcionar produto no bloco
    @Transactional
    public PurchaseItensResponse addProductToPurchase(Long familyId, Long purchaseId, PurchaseItensRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // busca a purchase e verifica se ela existe e pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        if (purchase.getPurchaseStatus() == PurchaseStatus.CLOSED){
            throw new BusinessException("Compra fechada");
        }

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca o produto e verifica se ele existe
        Product product = globalHelperService.getProductOrThrow(familyId, request.productId());

        if (product.getActive() == false) {
            throw new ProductNotFoundExeption();
        }

        // verifica se o produto ja existe dentro da maleta purchaseItens
       if (purchaseItensRepository.existsByProductIdAndPurchaseId(request.productId(), purchaseId)){
           throw new ConflictException("Esse produto ja existe na compra");
       }

        // criar o vinculo entre purchase e produto -> purchaseItens
        PurchaseItens purchaseItens = new PurchaseItens();

        purchaseItens.setProduct(product);
        purchaseItens.setPurchase(purchase);

        purchaseItens.setQuantity(request.quantity());
        purchaseItens.setUnitPrice(request.unitPrice());


        // adcionar do outro lado da relação
        purchase.getItens().add(purchaseItens);

        PurchaseItens savedItem = purchaseItensRepository.save(purchaseItens);
        addToPurchaseTotal(purchase, itemSubtotal(savedItem));

        return purchaseMapper.toPurchaseItensResponse(savedItem);


    }

    @Transactional
    public List<PurchaseItensResponse> addManyProductsToPurchase(Long familyId, Long purchaseId, PurchaseManyItensRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // busca a purchase e verifica se ela existe e pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        if (purchase.getPurchaseStatus() == PurchaseStatus.CLOSED){
            throw new BusinessException("Compra fechada");
        }

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // extrair a lista de ids dos produtos do request
        List<Long> productsIds = request.itensRequests().stream()
                .map(PurchaseItensRequest::productId)
                .toList();

        // verifica se eles existem e se estão ativos
        List<Product> products = globalHelperService.getManyProductOrThrow(productsIds, familyId);

        // verificar se não tem ids repetidos
        if(productsIds.size() != new HashSet<>(productsIds).size()){
            throw new ConflictException(
                    "Existem produtos repetidos na requisição"
            );
        }

        // verifica se ja existe
         if (purchaseItensRepository.existsByPurchaseIdAndProductIdIn(purchaseId, productsIds)){
             throw new ConflictException("Já existem produtos nessa compra");
         }


         // criar um map para pegar os produtos rapidamente
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        p -> p
                ));

         // percorrer o request e salvar todos de uma vez
        List<PurchaseItens> purchaseItensList = request.itensRequests().stream()
                .map(itemRequest -> {
                            Product product = productMap.get(itemRequest.productId());// para cada item request, pegar o produto pelo map, pelo id

                            // cria o vinculo
                            PurchaseItens item = new PurchaseItens();
                            item.setPurchase(purchase);
                            item.setProduct(product);
                            item.setQuantity(itemRequest.quantity());
                            item.setUnitPrice(itemRequest.unitPrice());

                            return item; // retorna para adcionar na lista
                        }

                ).toList();

        List<PurchaseItens> savedItems = purchaseItensRepository.saveAll(purchaseItensList);

        BigDecimal addedTotal = savedItems.stream()
                .map(this::itemSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        addToPurchaseTotal(purchase, addedTotal);

        return purchaseMapper.toPurchaseManyItensResponse(savedItems);
    }

    // ================ PUT ======================

    // membro admin pode editar purchase
    @Transactional
    public PurchaseResponse updatePurchase(Long familyId, long purchaseId,
                                           PurchaseUpdateRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // pega a purchase e ja verifica se ela exise e se pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        if (purchase.getPurchaseStatus() == PurchaseStatus.CLOSED){
            throw new BusinessException("Compra fechada");
        }

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        String name = purchase.getName();

        // atualiza os dados
        if (request.name() != null){
            purchase.setName(request.name());
            String message = "editou o nome da compra " + name + " para " + purchase.getName();
            historyService.createHistory(message, family, loggedUser, HistoryAction.UPDATED_PURCHASE);
        }

        return purchaseMapper.toPurchaseResponse(purchaseRepository.save(purchase));
    }

    // membro admin pode editar produtos dentro da purchase
    @Transactional
    public PurchaseItensResponse updateItemInPurchase(Long familyId, Long purchaseId
            , Long productId, PurchaseItenUpdateRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // pega a purchase e ja verifica se ela exise e se pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        if (purchase.getPurchaseStatus() == PurchaseStatus.CLOSED){
            throw new BusinessException("Compra fechada");
        }

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // pega o item
        PurchaseItens item = purchaseItensRepository.findByPurchaseIdAndProductId(purchaseId, productId)
                .orElseThrow(ProductNotFoundExeption::new);

        BigDecimal previousSubtotal = itemSubtotal(item);

        // atualiza
        if (request.unitPrice() != null){
            item.setUnitPrice(request.unitPrice());
        }

        if (request.quantity() != null){
            item.setQuantity(request.quantity());
        }

        PurchaseItens savedItem = purchaseItensRepository.save(item);
        addToPurchaseTotal(purchase, itemSubtotal(savedItem).subtract(previousSubtotal));

        return purchaseMapper.toPurchaseItensResponse(savedItem);

    }

    // usuario admin pode fechar a compra, não é permitido editar e nem apagar depois disso
    // cria a transação do tipo gasto automaticamente
    @Transactional
    public PurchaseTransactionResponse closePurchase(Long familyId, Long purchaseId,
    PurchaseTransactionRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // pega a purchase e ja verifica se ela exise e se pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        // não é possivel fechar uma compra que esta fechada
        if (purchase.getPurchaseStatus() == PurchaseStatus.CLOSED){
            throw new BusinessException("Compra fechada");
        }

        // não é possível fechar uma compra que não possui produtos
        if (!purchaseItensRepository.existsByPurchaseId(purchaseId)) {
            throw new BusinessException("Não é possível fechar uma compra sem itens.");
        }


        BigDecimal total = purchase.getTotal() != null ? purchase.getTotal() : BigDecimal.ZERO;

        // fecha
        purchase.setPurchaseStatus(PurchaseStatus.CLOSED);

        // criar a trasação
        TransactionResponse expenseTransaction = transactionService.createExpenseTransaction(purchase, total, request);

        String message = "fechou a compra " + purchase.getName();
        historyService.createHistory(message, family, loggedUser, HistoryAction.CLOSE_PURCHASE);

        return purchaseMapper.toPurchaseTransactionResponse(purchaseRepository.save(purchase), expenseTransaction);
    }

    // ================ DELETE ======================

    // membro admin pode deletar purchase
    @Transactional
    public void deletePurchase(Long familyId, Long purchaseId){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // pega a purchase e ja verifica se ela exise e se pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        if (purchase.getPurchaseStatus() == PurchaseStatus.CLOSED){
            throw new BusinessException("Compra fechada");
        }

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        String message = "deletou a compra " + purchase.getName();
        historyService.createHistory(message, family, loggedUser, HistoryAction.DELETED_PURCHASE);

        purchaseRepository.delete(purchase);
    }

    // membro admin pode deletar varias purchases
    @Transactional
    public void deleteManyPurchases(Long familyId, DeleteManyRequest request){
        if(request.ids().size() != new HashSet<>(request.ids()).size()){
            throw new ConflictException(
                    "Existem IDs repetidos na requisição"
            );
        }

        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // busca a lista no banco
        List<Purchase> purchases = purchaseRepository.findAllByFamilyIdAndIdIn(familyId, request.ids());

        // anyMatch para no primeiro closed
        if (purchases.stream()
                .anyMatch(p -> p.getPurchaseStatus() == PurchaseStatus.CLOSED)) {
            throw new BusinessException("Uma ou mais compras estão fechadas");
        }

        // verifica se esta faltando algum que não achou no banco
        if (purchases.size() != request.ids().size()){
            throw new PurchaseNotFoundException();
        }

        purchases.forEach(p -> {
            historyService.createHistory(
                    "deletou a compra " + p.getName(),
                    family,
                    loggedUser,
                    HistoryAction.DELETED_PURCHASE
            );
        });

        purchaseRepository.deleteAll(purchases);

    }

    // membro admin pode apagar produto dentro da purchase
    @Transactional
    public void deleteProductInPurchase (Long familyId, Long purchaseId, Long productId){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // pega a purchase e ja verifica se ela exise e se pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        if (purchase.getPurchaseStatus() == PurchaseStatus.CLOSED){
            throw new BusinessException("Compra fechada");
        }

        // pega o item
        PurchaseItens item = purchaseItensRepository.findByPurchaseIdAndProductId(purchaseId, productId)
                .orElseThrow(ProductNotFoundExeption::new);

        purchaseItensRepository.delete(item);
        addToPurchaseTotal(purchase, itemSubtotal(item).negate());
    }

    // membro admin pode apagar varios produtos dentro da purchase
    @Transactional
    public void deleteManyProductsInPurchase(Long familyId, Long purchaseId, DeleteManyRequest request ){
        if(request.ids().size() != new HashSet<>(request.ids()).size()){
            throw new ConflictException(
                    "Existem IDs repetidos na requisição"
            );
        }

        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        if (purchase.getPurchaseStatus() == PurchaseStatus.CLOSED){
            throw new BusinessException("Compra fechada");
        }

        List<PurchaseItens> items = purchaseItensRepository.findAllByPurchaseIdAndProductIdIn(purchaseId, request.ids());

        if (items.size() != request.ids().size()){
            throw new ProductNotFoundExeption();
        }

        purchaseItensRepository.deleteAll(items);
        BigDecimal removedTotal = items.stream()
                .map(this::itemSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        addToPurchaseTotal(purchase, removedTotal.negate());
    }


    private BigDecimal itemSubtotal(PurchaseItens item){
        return item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private void addToPurchaseTotal(Purchase purchase, BigDecimal amount){
        BigDecimal currentTotal = purchase.getTotal() != null ? purchase.getTotal() : BigDecimal.ZERO;
        purchase.setTotal(currentTotal.add(amount));
    }
}
