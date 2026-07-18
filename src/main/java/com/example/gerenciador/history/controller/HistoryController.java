package com.example.gerenciador.history.controller;


import com.example.gerenciador.history.dto.HistoryDeleteRequest;
import com.example.gerenciador.history.dto.HistoryResponse;
import com.example.gerenciador.history.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/my/family/{familyId}")
    public ResponseEntity<Page<HistoryResponse>> getMyHistory(
            @PathVariable Long familyId,
            @PageableDefault(
                    size = 12,
                    sort = "createdAt",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ){
        return ResponseEntity.ok(historyService.getMyHistory(familyId, pageable));
    }

    @DeleteMapping("/my/family/{familyId}/history/{historyId}/delete")
    public ResponseEntity<Void> deleteHistory(
            @PathVariable Long familyId,
            @PathVariable Long historyId

    ){
        historyService.deleteHistory(familyId, historyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/my/family/{familyId}/delete/many")
    public ResponseEntity<Void> deleteManyHistories(
            @PathVariable Long familyId,
            @RequestBody @Valid HistoryDeleteRequest request
            ){

        historyService.deleteManyHistories(familyId, request);
        return ResponseEntity.noContent().build();
    }


}
