package com.example.gerenciador.history.mapper;


import com.example.gerenciador.history.dto.HistoryResponse;
import com.example.gerenciador.history.entity.History;
import org.springframework.stereotype.Component;

@Component
public class HistoryMapper {

    public HistoryResponse toHistoryResponse(History h){
        return new HistoryResponse(
                h.getId(),
                h.getFamily().getId(),
                h.getUser().getId(),
                h.getUser().getName(),
                h.getDescription(),
                h.getAction(),
                h.getCreatedAt()
        );
    }
}
