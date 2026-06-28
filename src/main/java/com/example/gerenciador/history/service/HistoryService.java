package com.example.gerenciador.history.service;

import com.example.gerenciador.family.entity.Family;
import com.example.gerenciador.history.dto.HistoryResponse;
import com.example.gerenciador.history.entity.History;
import com.example.gerenciador.history.entity.HistoryAction;
import com.example.gerenciador.history.mapper.HistoryMapper;
import com.example.gerenciador.history.repository.HistoryRepository;
import com.example.gerenciador.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final HistoryMapper historyMapper;

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
}
