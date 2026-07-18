package com.example.gerenciador.history.service;

import com.example.gerenciador.exceptions.ConflictException;
import com.example.gerenciador.exceptions.HistoryNotFoundException;
import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.helpers.GlobalHelperService;
import com.example.gerenciador.history.dto.HistoryDeleteRequest;
import com.example.gerenciador.history.dto.HistoryResponse;
import com.example.gerenciador.history.entity.History;
import com.example.gerenciador.history.entity.HistoryAction;
import com.example.gerenciador.history.mapper.HistoryMapper;
import com.example.gerenciador.history.repository.HistoryRepository;
import com.example.gerenciador.security.SecurityService;
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
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;
    private final GlobalHelperService globalHelperService;
    private final SecurityService securityService;

    // metodo para criar novos historicos, exclusivo para salvar ações do membro admin na familia
    public HistoryResponse createHistory(String message, Family family, User member, HistoryAction action){
        History history = new History();

        history.setFamily(family);
        history.setUser(member);
        history.setDescription(message);
        history.setAction(action);
        history.setCreatedAt(LocalDateTime.now());

        return historyMapper.toHistoryResponse(historyRepository.save(history));
    }


    // membros da familia podem ver o historico
    public Page<HistoryResponse> getMyHistory(Long familyId, Pageable pageable) {
        User loggedUser = securityService.getLoggedUser();

        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se é membro
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return historyRepository.findAllByFamilyId(pageable, familyId)
                .map(historyMapper::toHistoryResponse);

    }

    // filtrar por HistoryAction
    public Page<HistoryResponse> historySearch(
            Long familyId, HistoryAction historyAction, String description,  Pageable pageable
    ){
        User loggedUser = securityService.getLoggedUser();

        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se é membro
        globalHelperService.getMemberOrThrow(family, loggedUser);

        return historyRepository.historySearch(familyId, historyAction, description, pageable)
                .map(historyMapper::toHistoryResponse);
    }

    // membro admin pode apagar historico
    public void deleteHistory(Long familyId, Long historyId){
        User loggedUser = securityService.getLoggedUser();

        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o membro é admin
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        History history = globalHelperService.getHistoryOrThrow(familyId, historyId);

        // deleta
        historyRepository.delete(history);
    }

    // membro admin pode deletar varios hsitoricos
    public void deleteManyHistories(Long familyId, HistoryDeleteRequest request){
        // verifica repetidos
        if (request.ids().size() != new HashSet<>(request.ids()).size()){
            throw new ConflictException("Há ids repetidos na requisição");
        }

        User loggedUser = securityService.getLoggedUser();

        Family family = globalHelperService.getFamilyOrThrow(familyId);

        // verifica se o membro é admin
        globalHelperService.getAdminMemberOrThrow(family, loggedUser);

        // acha a lista de historicos
        List<History> histories = historyRepository.findAllByFamilyIdAndIdIn(familyId, request.ids());

        // verifica se esta faltando
        if (histories.size() != request.ids().size()){
            throw new HistoryNotFoundException();
        }

        // deleta
        historyRepository.deleteAll(histories);


    }
}
