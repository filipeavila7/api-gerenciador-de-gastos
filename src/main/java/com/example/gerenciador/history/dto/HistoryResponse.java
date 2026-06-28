package com.example.gerenciador.history.dto;

import com.example.gerenciador.history.entity.HistoryAction;

import java.time.LocalDateTime;

public record HistoryResponse(
        Long id,
        Long familyId,
        String familyName,
        Long userId,
        String userName,
        String description,
        HistoryAction action,
        LocalDateTime createdAt

) {
}
