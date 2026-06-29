package com.example.gerenciador.history.dto;

import java.util.List;

public record HistoryDeleteRequest(
        List<Long> ids
) {
}
