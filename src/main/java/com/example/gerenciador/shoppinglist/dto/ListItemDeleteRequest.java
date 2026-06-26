package com.example.gerenciador.shoppinglist.dto;

import java.util.List;

public record ListItemDeleteRequest(
        List<Long> ids
) {
}
