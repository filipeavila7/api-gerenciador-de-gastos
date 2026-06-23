package com.example.gerenciador.purchase.service;

import com.example.gerenciador.exceptions.BusinessException;
import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.exceptions.ProductNotFoundExeption;
import com.example.gerenciador.exceptions.PurchaseNotFoundException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
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

    // ================ GET ======================

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

        purchaseRepository.save(purchase);

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

        purchaseItensRepository.save(purchaseItens);

        return purchaseMapper.toPurchaseItensResponse(purchaseItens);


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

         // percorrer o request e salavr todos de uma vez
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

        return purchaseMapper.toPurchaseManyItensResponse(purchaseItensRepository
                        .saveAll(purchaseItensList));
    }

    // ================ PUT ======================

    // membro admin pode editar purchase
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



        // atualiza os dados
        if (request.name() != null){
            purchase.setName(request.name());
        }

        return purchaseMapper.toPurchaseResponse(purchaseRepository.save(purchase));
    }

    // membro admin pode editar produtos dentro da purchase
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

        // atualiza
        if (request.unitPrice() != null){
            item.setUnitPrice(request.unitPrice());
        }

        if (request.quantity() != null){
            item.setQuantity(request.quantity());
        }

        return purchaseMapper.toPurchaseItensResponse(purchaseItensRepository.save(item));

    }

    // usuario admin pode fechar a compra, não é permitido editar e nem apagar depois disso
    // cria a transação do tipo gasto automaticamente
    @Transactional
    public PurchaseTransactionResponse closePurchase(Long familyId, Long purchaseId,
    PurchaseTransactionRequest request){
        User loggedUser = securityService.getLoggedUser();

        // busca a familia e verifica se ela existe
        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // pega a purchase e ja verifica se ela exise e se pertence aquela familia
        Purchase purchase = globalHelperService.getPurchaseOrThrow(familyId, purchaseId);

        // não é possivel fechar uma compra que esta fechada
        if (purchase.getPurchaseStatus() == PurchaseStatus.CLOSED){
            throw new BusinessException("Compra fechada");
        }

        // não é possível fechar uma compra que não possui produtos
        if (purchase.getItens().isEmpty()) {
            throw new BusinessException("Não é possível fechar uma compra sem itens.");
        }

        // verifica se o usuario é admin ou pertence a familia
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // calcula total e salva no banco
        BigDecimal total = calculatePurchaseTotal(purchase);
        purchase.setTotal(total);

        // fecha
        purchase.setPurchaseStatus(PurchaseStatus.CLOSED);

        // criar a trasação
        TransactionResponse expenseTransaction = transactionService.createExpenseTransaction(purchase, total, request);


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
    }

    // membro admin pode apagar varios produtos dentro da purchase
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
    }


    private BigDecimal calculatePurchaseTotal(Purchase p){
        return p.getItens()
                .stream()
                .map(item ->
                        item.getUnitPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())) // multiplica o unit price pela quantidade
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
}
